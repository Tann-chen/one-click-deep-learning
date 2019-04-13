package acceler.ocdl.service;

import acceler.ocdl.exception.HdfsException;
import acceler.ocdl.exception.KuberneteException;
import acceler.ocdl.model.User;

public interface KubernetesService {

    String launchGpuContainer(User user) throws KuberneteException, HdfsException;

    String launchCpuContainer(User user) throws KuberneteException, HdfsException;

    void releaseDockerContainer(User user) throws KuberneteException;
}
