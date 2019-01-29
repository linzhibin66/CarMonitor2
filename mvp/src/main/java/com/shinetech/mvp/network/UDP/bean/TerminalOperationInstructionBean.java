package com.shinetech.mvp.network.UDP.bean;

import com.shinetech.mvp.network.UDP.InfoTool.Protocol;

/**
 * Created by ljn on 2017/2/13.
 * 终端操作指令
 */
public class TerminalOperationInstructionBean extends BaseVo{

    /**
     * 车牌
     */
    private String platenumber;

    /**
     * 操作指令
     */
    private byte instruction;

    /******************************  result  ***************************************/

    /**
     * 返回车牌号
     */
    private String resultPlateNumber;

    /**
     * 返回操作指令
     */
    private byte resultInstruction;

    /**
     * 操作结果
     */
    private byte result;

    /**
     * 失败原因
     */
    private String errorMessage;

    public TerminalOperationInstructionBean(byte instruction, String platenumber) {
        this.instruction = instruction;
        this.platenumber = platenumber;
        requestProtocolHead = Protocol.PROTOCOL_OPERATIONSTRUCTION_BEAT;
        responseProtocolHead = Protocol.PROTOCOL_OPERATIONSTRUCTION_BEAT_RESULT;
    }

    @Override
    public Object[] getProperties() {
        return new Object[]{platenumber, instruction};
    }

    @Override
    public void setProperties(Object[] properties) {
        this.resultPlateNumber = parseString((byte[]) properties[0]);
        this.resultInstruction = (byte) properties[1];
        this.result = (byte) properties[2];
        this.errorMessage = parseString((byte[]) properties[3]);

    }

    @Override
    public short[] getDataTypes() {
        return new short[]{STRING, BYTE, BYTE, STRING};
    }
}
