package acceler.ocdl.controller;

import acceler.ocdl.dto.IncomeModelDto;
import acceler.ocdl.dto.ModelDto;
import acceler.ocdl.dto.Response;

import acceler.ocdl.model.Model;
import acceler.ocdl.persistence.ModelCrud;
import acceler.ocdl.persistence.ModelTypeCrud;
import acceler.ocdl.service.ModelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static acceler.ocdl.dto.Response.*;


@Controller
@RequestMapping(path = "/approval")
public class ApprovalController {
    private static final Logger logger = LoggerFactory.getLogger(ApprovalController.class);
    public static final String stagePath = "/home/ec2-user/stage/";

    @Autowired
    private ModelTypeCrud modelTypeCrud;

    @Autowired
    private ModelCrud modelCrud;

    @Autowired
    private ModelService modelService;


    @ResponseBody
    @RequestMapping(path = "/models", method = RequestMethod.GET)
    public final Response getModelList() {
        Map<String, List<ModelDto>> models = new HashMap<>();

        List<ModelDto> newModels = modelCrud.getModels(Model.Status.NEW);
        models.put("newModels", newModels);

        List<ModelDto> approvalModels = modelCrud.getModels(Model.Status.APPROVED);
        models.put("approvalModels", approvalModels);

        List<ModelDto> rejectModels = modelCrud.getModels(Model.Status.REJECTED);
        models.put("rejectedModels", rejectModels);

        return Response.getBuilder().setCode(Response.Code.SUCCESS).setData(models).build();
    }

    @ResponseBody
    @RequestMapping(path = "/modeltypes", method = RequestMethod.GET)
    public final Response getModeltype() {
        List<String> modelTypes = modelTypeCrud.getModelTypes();
        return Response.getBuilder()
                .setCode(Response.Code.SUCCESS)
                .setData(modelTypes)
                .build();
    }

    @ResponseBody
    @RequestMapping(path = "/{modelName}", method = RequestMethod.PUT)
    public final Response pushDecision(@PathVariable("modelName") String modelName, @RequestBody IncomeModelDto incomeModelDto) {
        Builder responseBuilder = getBuilder();

        // if corresponding model file exit
        if (!modelCrud.modelExist(modelName, incomeModelDto.getDestStatus().toLowerCase())) {
            responseBuilder.setCode(Response.Code.ERROR).setMessage("Cannot find the model File");
        }

        Path source = Paths.get(stagePath, incomeModelDto.getPreStatus(), incomeModelDto.getModelName());

        String newModelName = getNewModelName(incomeModelDto, source);
        Path target = Paths.get(stagePath, incomeModelDto.getDestStatus(), newModelName);

        boolean success = modelCrud.moveModel(source, target);

        if (success) {
            responseBuilder.setCode(Response.Code.SUCCESS);
        } else {
            responseBuilder.setCode(Response.Code.ERROR).setMessage("Fail to move file");
        }

        return responseBuilder.build();
    }

    private String getNewModelName(IncomeModelDto incomeModelDto, Path source) {
        String[] modelInfo = incomeModelDto.getModelName().split("_");
        StringBuilder newModelName = new StringBuilder();

        // when new or reject, the file name will be FN_TS.suffix
        newModelName.append(modelInfo[0]);

        long time = new Date().getTime();
        newModelName.append(String.valueOf(time));

        // when approval, the file name will be FN_TS_MT_V*.*.suffix
        if (incomeModelDto.getDestStatus().equals("approval")) {
            newModelName.append(incomeModelDto.getModelType());
            int bigVersion = 1;
            int smallVersion = 0;

            int[] currentVersion = modelTypeCrud.getVersion(incomeModelDto.getModelType());
            int currentBigVersion = currentVersion[0];
            int currentSmallVersion = currentVersion[1];

            if (incomeModelDto.getBigVersion() == 1) {
                if (currentBigVersion >= 0) {
                    bigVersion = currentBigVersion + 1;
                } else {
                    bigVersion = 1;
                }
                smallVersion = 0;
            } else {
                if (currentBigVersion >= 0) {
                    bigVersion = currentBigVersion;
                } else {
                    currentBigVersion = 0;
                }

                if (currentSmallVersion >= 0) {
                    smallVersion = currentSmallVersion + 1;
                } else {
                    smallVersion = 1;
                }
            }

            modelTypeCrud.setVersion(incomeModelDto.getModelType(), bigVersion, smallVersion);
            newModelName.append("_v");
            newModelName.append(bigVersion);
            newModelName.append(".");
            newModelName.append(smallVersion);

            String newPushName = getNewPushedModelName(incomeModelDto.getModelType(), String.valueOf(bigVersion), String.valueOf(smallVersion));
            modelService.pushModel(source.toString(), newPushName);
        }

        return newModelName.toString();
    }

    private String getNewPushedModelName(String modelType, String bigVersion, String smallVersion) {
        StringBuilder newModelName = new StringBuilder();

        newModelName.append(modelType);
        newModelName.append("_v");
        newModelName.append(bigVersion);
        newModelName.append(".");
        newModelName.append(smallVersion);

        return newModelName.toString();
    }

}
