package acceler.ocdl.service;

import acceler.ocdl.model.Algorithm;
import acceler.ocdl.model.Model;
import acceler.ocdl.model.NewModel;
import acceler.ocdl.model.User;

import java.util.List;

public interface ModelService {

    /**
     * move the model from userspace to stage space, name is formatted at the same time
     * the status of moved model is new, and the format of the fileName = "modelName + timestamp" + "suffix"
     * @param user used for get userspace path, userspacePath = "HDFS path" + "projectName-userID"
     */
    void initModelToStage(User user);


    /**
     * approval model
     * move model file from folder "new"  to folder "approval" and update the model object
     * the format of the fileName = "modelName" + "timestamp" + "algorithm" + "version" + "suffix"
     * the format of version = "v*.*"
     * @param model model that need to approve
     * @param algorithmName the name of algorithm that chose when approve
     * @param version the version that chose when approve
     */
    void approveModel(NewModel model, String algorithmName, Algorithm.UpgradeVersion version);

    /**
     * reject model
     * move model file from folder "new"  to folder "reject" and update the model object
     * he format of the fileName = "modelName" + "timestamp" + "suffix"
     * @param model model that need to reject
     */
    void rejectModel(NewModel model);

    /**
     * reject model
     * move model file from folder "approval" or "reject"  to folder "new" and update the model object
     * the format of the fileName = "modelName" + "timestamp"
     * @param model model that need to undo
     */
    void undo(Model model);

    /**
     * push model to git repository
     * copy model file from folder "approval" to git repository, and git push
     * @param modelName = "algorithm" + "version" + "suffix" (the format of version = "v*.*")
     */
    void pushModelToGit(String modelName);

    /**
     * get models by status
     * @param status specific status
     * @return list of Model
     */
    List<Model> getModelsByStatus(Model.Status status);

    /**
     * verify is the file of model is exist in status folder
     * @param modelName the name of the model
     * @param status the status of the model
     * @return true exist the model file; else not exist.
     */
    boolean existModel(String modelName, Model.Status status);
}
