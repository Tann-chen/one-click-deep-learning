package acceler.ocdl.model;

import acceler.ocdl.utils.TimeUtil;

import java.util.Date;

public class ApprovedModel extends Model implements Cloneable {
    private Long releasedVersion;
    private Long cachedVersion;
    private Date approvedTime;


    public ApprovedModel() {
        super();
        this.status = Status.APPROVED;
    }

    public ApprovedModel deepCopy() {
        ApprovedModel copy = new ApprovedModel();

        copy.setModelId(this.modelId);
        copy.setName(this.name);
        copy.status = Status.APPROVED;
        copy.setReleasedVersion(this.releasedVersion);
        copy.setCachedVersion(this.cachedVersion);
        copy.setApprovedTime(this.approvedTime);

        return copy;
    }

    public NewModel convertToNewModel() {
        NewModel newModel = new NewModel();

        newModel.setModelId(this.modelId);
        newModel.setName(this.name);
        newModel.setCommitTime(TimeUtil.currentTime());

        return new NewModel();
    }

    public Long getReleasedVersion() {
        return this.releasedVersion;
    }

    public Long getCachedVersion() {
        return this.cachedVersion;
    }

    public Date getApprovedTime() {
        return this.approvedTime;
    }

    public void setApprovedTime(Date approvedTime) {
        this.approvedTime = approvedTime;
    }

    void setReleasedVersion(Long releasedVersion) {
        this.releasedVersion = releasedVersion;
    }

    void setCachedVersion(Long cachedVersion) {
        this.cachedVersion = cachedVersion;
    }
}
