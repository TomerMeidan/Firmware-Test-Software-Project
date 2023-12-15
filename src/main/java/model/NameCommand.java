package model;

import org.json.simple.JSONObject;

public class NameCommand {

    private String name;
    private String opCode;
    private String dataSendFormat;
    private String dataSendUnit;
    private String returnFormat;
    private String dataReturnUnit;
    private String description;
    private String remark;

    // Constructors, getters, and setters

    public NameCommand(String name, String opCode, String dataSendFormat, String dataSendUnit,
                        String returnFormat, String dataReturnUnit, String description, String remark) {
        this.name = name;
        this.opCode = opCode;
        this.dataSendFormat = dataSendFormat;
        this.dataSendUnit = dataSendUnit;
        this.returnFormat = returnFormat;
        this.dataReturnUnit = dataReturnUnit;
        this.description = description;
        this.remark = remark;
    }

    // Getters and setters

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOpCode() {
        return opCode;
    }

    public void setOpCode(String opCode) {
        this.opCode = opCode;
    }

    public String getDataSendFormat() {
        return dataSendFormat;
    }

    public void setDataSendFormat(String dataSendFormat) {
        this.dataSendFormat = dataSendFormat;
    }

    public String getDataSendUnit() {
        return dataSendUnit;
    }

    public void setDataSendUnit(String dataSendUnit) {
        this.dataSendUnit = dataSendUnit;
    }

    public String getReturnFormat() {
        return returnFormat;
    }

    public void setReturnFormat(String returnFormat) {
        this.returnFormat = returnFormat;
    }

    public String getDataReturnUnit() {
        return dataReturnUnit;
    }

    public void setDataReturnUnit(String dataReturnUnit) {
        this.dataReturnUnit = dataReturnUnit;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    @Override
    public String toString() {
        return "MotorCommand{" +
                "name='" + name + '\'' +
                ", opCode='" + opCode + '\'' +
                ", dataSendFormat='" + dataSendFormat + '\'' +
                ", dataSendUnit='" + dataSendUnit + '\'' +
                ", returnFormat='" + returnFormat + '\'' +
                ", dataReturnUnit='" + dataReturnUnit + '\'' +
                ", description='" + description + '\'' +
                ", remark='" + remark + '\'' +
                '}';
    }
}

