package top.yifan.masterha.node;

import top.yifan.masterha.core.HAState;

import java.time.Instant;

/**
 * MasterNodeSummary 节点信息
 *
 * @author star
 */
public class MasterNodeSummary {

    private String nodeId;
    private String endpoint;
    private NodeMode mode;
    private NodeState state;
    private HAState haState;
    private Instant startTime;

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

    public NodeMode getMode() {
        return mode;
    }

    public void setMode(NodeMode mode) {
        this.mode = mode;
    }

    public NodeState getState() {
        return state;
    }

    public void setState(NodeState state) {
        this.state = state;
    }

    public HAState getHaState() {
        return haState;
    }

    public void setHaState(HAState haState) {
        this.haState = haState;
    }

    public Instant getStartTime() {
        return startTime;
    }

    public void setStartTime(Instant startTime) {
        this.startTime = startTime;
    }
}
