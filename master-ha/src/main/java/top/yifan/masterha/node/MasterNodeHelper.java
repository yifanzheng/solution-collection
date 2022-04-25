package top.yifan.masterha.node;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONWriter;
import top.yifan.masterha.core.HAState;
import top.yifan.masterha.util.IDUtil;

/**
 * MasterNodeHelper
 *
 * @author star
 */
public class MasterNodeHelper {

    private static final  String ID;
    private static final MasterNodeSummary NODE_SUMMARY;

    private MasterNodeHelper() {
    }

    static {
        ID = IDUtil.generateShortUUID();
        NODE_SUMMARY = new MasterNodeSummary(ID);
    }

    public static void flushNodeHAState(HAState haState) {
        NODE_SUMMARY.setHaState(haState);
    }

    public static MasterNodeSummary getNodeSummary() {
        return NODE_SUMMARY;
    }

    public static String getNodeSummaryJSON() {
        return JSON.toJSONString(NODE_SUMMARY, JSONWriter.Feature.WriteEnumUsingToString);
    }

}
