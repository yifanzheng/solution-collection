package top.yifan.masterha.core;

/**
 * MasterNodeSummary 节点信息
 *
 * @author star
 */
public class MasterNodeSummary {

    private String nodeId;
    private String endpoint;
    private String mode;
    private HAState state;

    public MasterNodeSummary(String nodeId) {
        this.nodeId = nodeId;
    }

    public String getNodeId() {
        return nodeId;
    }

    public void setNodeId(String nodeId) {
        this.nodeId = nodeId;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public HAState getState() {
        return state;
    }

    public void setState(HAState state) {
        this.state = state;
    }
}
