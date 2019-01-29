package com.shinetech.mvp.network.UDP.InfoTool;

import android.text.TextUtils;

import com.shinetech.mvp.DB.bean.MaintenanceOrderInfoBean;
import com.shinetech.mvp.MainApplication;
import com.shinetech.mvp.network.UDP.bean.BaseVo;
import com.shinetech.mvp.network.UDP.bean.orderBean.AttachmentItemVo;
import com.shinetech.mvp.network.UDP.bean.orderBean.CreateOrderDataVo;
import com.shinetech.mvp.network.UDP.bean.orderBean.MaintenanceOrderProgress;
import com.shinetech.mvp.network.UDP.bean.orderBean.SelectOrderByNumberVo;
import com.shinetech.mvp.utils.ByteUtil;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by ljn on 2018-01-03.
 */

public class DecodeUDPDataTool {

    private static boolean debug = false;

    private static LinkedHashMap<String,OrderContentListItemData> baseDecodeOrderContent(byte[] data, String queryName){
        int index = 0;

        byte[] bytes;

        int listSize = getInt(data, index);

        index += 4;

        LinkedHashMap<String,OrderContentListItemData> contentMap = new LinkedHashMap<>();


        if(listSize <= 0){
            return contentMap;
        }

        boolean isQuery = !TextUtils.isEmpty(queryName);

        for(int i = 0; i<listSize; i++){

            //获取内容列表条目名称
            String itemDataName = null;

            if(data.length-index>0) {
                int len = data[index++] & 0xFF;
                bytes = new byte[len];
                System.arraycopy(data, index, bytes, 0, len);
                itemDataName = ByteUtil.stringFromBytes(bytes, 0, bytes.length);
                index += len;
            }

            if(debug){
                System.out.println(" - - - - - - - - - - orderContentList : "+ itemDataName);
            }

            //获取Data 长度
            int dataLength = getInt(data, index);

            index += 4;

            bytes = new byte[dataLength];
            System.arraycopy(data, index, bytes, 0, dataLength);

            if(isQuery){
                if(queryName.equals(itemDataName)){
                    OrderContentListItemData orderContentListItemData = decodeItemData(bytes);
                    contentMap.put(itemDataName, orderContentListItemData);
                    return contentMap;
                }

            }else{

                OrderContentListItemData orderContentListItemData = decodeItemData(bytes);

                contentMap.put(itemDataName, orderContentListItemData);
            }


            index += dataLength;
        }

        return contentMap;
    }

    /**
     * 解析所有数据
     * @param data
     * @return
     */
    public static OrderContentList decodeOrderContent(byte[] data){


        OrderContentList orderContentList = new OrderContentList();

        LinkedHashMap<String, OrderContentListItemData> stringOrderContentListItemDataLinkedHashMap = baseDecodeOrderContent(data, null);

        orderContentList.contentListLength = stringOrderContentListItemDataLinkedHashMap.size();

        orderContentList.contentList.putAll(stringOrderContentListItemDataLinkedHashMap);

        return orderContentList;

    }

    /**
     *
     * @param data  内容列表DATA
     * @param name 工单内容列表中的名称
     * @return OrderContentListItemData
     */
    public static OrderContentListItemData decodeItemByDataName(byte[] data, String name){

        LinkedHashMap<String, OrderContentListItemData> stringOrderContentListItemDataLinkedHashMap = baseDecodeOrderContent(data, name);

        return stringOrderContentListItemDataLinkedHashMap.get(name);
    }

    private static int getInt(byte[] data, int index){

        byte[] bytes = new byte[4];

        if(data.length-index>0) {

            if (data.length - index < 4) {

                System.arraycopy(data, index, bytes, 0, data.length - index);

            } else {
                System.arraycopy(data, index, bytes, 0, 4);
            }
        }
        int dataLength = ByteUtil.intFromBytes(bytes);

        return dataLength;
    }


    private static OrderContentListItemData decodeItemData(byte[] itemData){
        OrderContentListItemData mOrderContentListItemData = new OrderContentListItemData();
        int index = 0;
        mOrderContentListItemData.fieldListContentLenght = getInt(itemData, index);

        index += 4;

        mOrderContentListItemData.fieldNameLenght = itemData[index++] & 0xFF;

        for(int i = 0; i<=mOrderContentListItemData.fieldListContentLenght;i++){

            List<String> fieldListContent = new ArrayList<>();
            for(int j = 0; j<mOrderContentListItemData.fieldNameLenght; j++){

                String itemDataName = null;

                if(itemData.length-index>0) {
                    int len = itemData[index++] & 0xFF;
                    byte[] bytes = new byte[len];
                    System.arraycopy(itemData, index, bytes, 0, len);
                    itemDataName = ByteUtil.stringFromBytes(bytes, 0, bytes.length);
                    index += len;
                }

                if(i == 0 && !TextUtils.isEmpty(itemDataName)){
                    mOrderContentListItemData.fieldNameList.add(itemDataName);
                    if(debug){
                        System.out.println(" - - - - - fieldName : "+ itemDataName);
                    }
                }else {
                    if(!TextUtils.isEmpty(itemDataName)) {
                        fieldListContent.add(itemDataName);
                        if (debug) {
                            System.out.println(" - - - - - fieldListContent : " + itemDataName);
                        }
                    }else{
                        fieldListContent.add("");
                        if (debug) {
                            System.out.println(" - - - - - fieldListContent : is null" );
                        }
                    }
                }
            }

            if(i > 0){
                mOrderContentListItemData.fieldListContent.add(fieldListContent);
            }

        }
        return mOrderContentListItemData;
    }

    public static List<AccessoryList> decodeAccessory(byte[] data){
        ArrayList<AccessoryList> accessoryList = new ArrayList<>();
        int index = 0;
        //获取Data 长度
        int dataLength = getInt(data, index);

        index += 4;


        for(int i = 0 ;i<dataLength; i++){

            byte[] bytes;
            AccessoryList mAccessoryList = new AccessoryList();

            for(int j = 0 ;j<4;j++){

                String itemDataName = null;

                if(data.length-index>0) {
                    int len = data[index++] & 0xFF;
                    bytes = new byte[len];
                    System.arraycopy(data, index, bytes, 0, len);
                    itemDataName = ByteUtil.stringFromBytes(bytes, 0, bytes.length);
                    index += len;
                }
                switch (j){
                    case 0:
                        mAccessoryList.fileType = itemDataName;
                        break;
                    case 1:
                        mAccessoryList.fileName = itemDataName;
                        break;
                    case 2:
                        mAccessoryList.babelte = itemDataName;
                        break;
                    case 3:
                        mAccessoryList.uploadTime = itemDataName;
                        break;
                }
            }
            accessoryList.add(mAccessoryList);
        }

        return accessoryList;
    }

    public static List<ProcessList> decodeProcess(byte[] data){
        ArrayList<ProcessList> processList = new ArrayList<>();
        int index = 0;
        //获取Data 长度
        int dataLength = getInt(data, index);

        index += 4;


        for(int i = 0 ;i<dataLength; i++){

            byte[] bytes;
            ProcessList mProcessList = new ProcessList();

            for(int j = 0 ;j<5;j++){

                String itemDataName = null;

                if(j != 3) {
                    if (data.length - index > 0) {
                        int len = data[index++] & 0xFF;
                        bytes = new byte[len];
                        System.arraycopy(data, index, bytes, 0, len);
                        itemDataName = ByteUtil.stringFromBytes(bytes, 0, bytes.length);
                        index += len;
                    }

                    switch (j){
                        case 0:
                            mProcessList.procedureName = itemDataName;
                            break;
                        case 1:
                            mProcessList.disposePerson = itemDataName;
                            break;
                        case 2:
                            mProcessList.disposeTime = itemDataName;
                            break;
                        case 4:
                            mProcessList.disposeOpinion = itemDataName;
                            break;
                    }

                }else{
                    mProcessList.disposeResult = data[index++];
                }
            }
            processList.add(mProcessList);
        }

        return processList;
    }




    public static class OrderContentListItemData{
        int fieldNameLenght;
        int fieldListContentLenght;
        List<String> fieldNameList = new ArrayList<>();
        List<List<String>> fieldListContent = new ArrayList<>();

        public List<String> getFieldNameList() {
            return fieldNameList;
        }

        public List<List<String>> getFieldListContent() {
            return fieldListContent;
        }

        public String getFistValue(String filedName){
            return getValue(filedName, 0);
        }

        public String getValue(String filedName, int itemIndex){

            int filedNameIndex = fieldNameList.indexOf(filedName);
            if(filedNameIndex < 0){
                return null;
            }

            if(fieldListContent.size() == 0){
                return null;
            }

            if(fieldListContent.size()> itemIndex) {

                List<String> valuesList = fieldListContent.get(itemIndex);

                if (valuesList.size() == fieldNameList.size()) {
                    return valuesList.get(filedNameIndex);
                }
            }

            return null;

        }

        public boolean hasContent(){
            if(fieldListContent.size() >0  && fieldListContent.get(0).size()>0){
                return true;
            }

            return false;

        }

        public int getSize(){
            return fieldListContent.size();
        }

        public List<String> getValuesItem(int index){
            if(fieldListContent.size()> index) {
                return fieldListContent.get(index);
            }

            return null;

        }
    }

    public static class OrderContentList{
        int contentListLength;
         LinkedHashMap<String,OrderContentListItemData> contentList = new LinkedHashMap<>();

        public LinkedHashMap<String, OrderContentListItemData> getContentList() {
            return contentList;
        }
    }

    static class AccessoryList{
        String fileType;
        String fileName;
        String babelte;
        String uploadTime;
    }

    static class ProcessList{
        String procedureName;
        String disposePerson;
        String disposeTime;
        byte disposeResult;
        String disposeOpinion;
    }

    public static byte[] createOrderListDATA(String[] fieldNameList, List<List<String>> fieldContentList){

        int fieldContentLength = fieldContentList.size();
        int fieldlength = fieldNameList.length;

        byte[] tempbuffer = new byte[1400];

        int index = 0;

        //记录条数
        byte[] bytes = ByteUtil.intToBytes(fieldContentLength);
        System.arraycopy(bytes, 0, tempbuffer, index, bytes.length);
        index += bytes.length;

        //字段条数
        tempbuffer[index++] = (byte) fieldlength;
        for(int j = 0; j<=fieldContentLength; j++) {

            for (int i = 0; i < fieldlength; i++) {
                String value;

                if(j == 0) {
                    value = fieldNameList[i];
                }else{
                    List<String> fieldContentItem = fieldContentList.get(j - 1);
                    if(fieldContentItem.size() > i) {
                        value = fieldContentItem.get(i);
                    }else{
                        value = "";
                    }
                }

                if (TextUtils.isEmpty(value)) {
                    bytes = null; //字符串为空，设置bytes为空，避免复制无效的数据到buffer里面去
                } else {
                    bytes = ByteUtil.stringToBytes(value);
                }

                //若buffer长度不够，需要增长
                if ((tempbuffer.length - index - 1) < (bytes == null ? 0 : (bytes.length + 1))) {
                    byte[] buffer = new byte[tempbuffer.length + 1400];
                    System.arraycopy(tempbuffer, 0, buffer, 0, tempbuffer.length);
                    tempbuffer = buffer;
                }

                if (bytes != null) {
                    tempbuffer[index++] = (byte) bytes.length;
                    System.arraycopy(bytes, 0, tempbuffer, index, bytes.length);
                    index += bytes.length;
                } else {
                    tempbuffer[index++] = 0;
                }

            }
        }

        byte[] dataBuffer = new byte[index];
        System.arraycopy(tempbuffer, 0, dataBuffer, 0, index);

        return dataBuffer;
    }

    public static byte[] createOrderContentListDATA(Map<String,byte[]> contentMap){

        int length = contentMap.size();

        byte[] tempbuffer = new byte[1400];

        //总条数
        byte[] bytes = ByteUtil.intToBytes(length);

        int index = 0;

        //添加总数
        System.arraycopy(bytes, 0, tempbuffer, index, bytes.length);

        index += bytes.length;

        Set<Map.Entry<String, byte[]>> entries = contentMap.entrySet();
        Iterator<Map.Entry<String, byte[]>> iterator = entries.iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, byte[]> item = iterator.next();
            String name = item.getKey();
            byte[] data = item.getValue();
            int itemLength = 0;
            if (TextUtils.isEmpty(name)) {
                bytes = null; //字符串为空，设置bytes为空，避免复制无效的数据到buffer里面去
            } else {
                bytes = ByteUtil.stringToBytes(name);
                itemLength += (bytes.length+1);
            }

            itemLength += (data.length+4);

            //若buffer长度不够，需要增长
            if ((tempbuffer.length - index - 1) < itemLength) {
                byte[] buffer;
                if(itemLength>1400){
                    buffer = new byte[tempbuffer.length + itemLength + 1400];
                }else{
                    buffer = new byte[tempbuffer.length + 1400];
                }

                System.arraycopy(tempbuffer, 0, buffer, 0, tempbuffer.length);
                tempbuffer = buffer;
            }

            //添加内容名称长度
            if(bytes == null) {
                tempbuffer[index++] = 0;
            }else{
                tempbuffer[index++] = (byte) bytes.length;
            }

            //添加内容名称
            System.arraycopy(bytes, 0, tempbuffer, index, bytes.length);
            index += bytes.length;

            //添加 DATA 的长度
            int dataLength = data.length;
            bytes = ByteUtil.intToBytes(dataLength);
            System.arraycopy(bytes, 0, tempbuffer, index, bytes.length);
            index += bytes.length;

            //添加 DATA 的长度
            System.arraycopy(data, 0, tempbuffer, index, data.length);
            index += data.length;

        }

        byte[] sendBuffer = new byte[index];

        System.arraycopy(tempbuffer, 0, sendBuffer, 0, index);

        return sendBuffer;

    }

    public static byte[] getOrderDate(CreateOrderDataVo mCreateOrderDataVo){

        return baseVoToByteArray(mCreateOrderDataVo);
    }

    private static byte[] baseVoToByteArray( BaseVo mBaseVo){

        Object[] properties = mBaseVo.getProperties();

        //根据属性的类型，编码数据内容
        if(properties!=null){
            Object property;
            int index = 0;
            byte[] tempBuffer = new byte[1400];
            byte[] bytes;
            for (int i = 0; i < properties.length; i++) {
                property = properties[i];

                if (property instanceof Byte) {
                    tempBuffer[index++] = ((Byte) property);
                    //if(debug)System.out.println(" property Byte HexString : " + byte2HexString(((Byte) property)));
                    continue;
                } else if (property instanceof Short) {
                    bytes = ByteUtil.shortToBytes(((Short) property));
                    //if(debug)System.out.println(" Short Byte HexString : " + bytes2HexString(bytes));
                } else if (property instanceof Integer) {
                    bytes = ByteUtil.intToBytes(((Integer) property));
                    // if(debug)System.out.println(" Integer Byte HexString : " + bytes2HexString(bytes));
                } else if (property instanceof String) {
                    String value = ((String) property);
                    if (TextUtils.isEmpty(value)) {
                        bytes = null; //字符串为空，设置bytes为空，避免复制无效的数据到buffer里面去
                        tempBuffer[index++] = 0;
                    } else {
                        bytes = ByteUtil.stringToBytes(value);
                        tempBuffer[index++] = (byte) bytes.length;
                        //if(debug)System.out.println(" String length : " + bytes.length);
                        //if(debug)System.out.println(" String Byte HexString : " + bytes2HexString(bytes));
                    }

                }else if(property instanceof byte[]){
                    bytes = (byte[]) property;
                    int length = bytes.length;
                    byte[] lengthbytes = ByteUtil.intToBytes(length);
                    System.arraycopy(lengthbytes, 0, tempBuffer, index, lengthbytes.length);
                    index += lengthbytes.length;
                } else {
                    if (property == null) {
                        throw new NullPointerException(mBaseVo.getClass().getSimpleName() + "的getProperties()方法返回参数的第" + i + "参数为空");
                    } else {
                        throw new IllegalArgumentException("无法识别的数据类型! " + property.toString());
                    }
                }

                if (bytes != null) {

                    //若buffer长度不够，需要增长
                    int itemLength = (bytes.length +4);
                    if ((tempBuffer.length - index - 1) < itemLength) {
                        byte[] buffer;
                        if(itemLength>1400){
                            buffer = new byte[tempBuffer.length + itemLength + 1400];
                        }else{
                            buffer = new byte[tempBuffer.length + 1400];
                        }

                        System.arraycopy(tempBuffer, 0, buffer, 0, tempBuffer.length);
                        tempBuffer = buffer;
                    }

                    System.arraycopy(bytes, 0, tempBuffer, index, bytes.length);
                    index += bytes.length;
                }
            }

            byte[] sendBuffer = new byte[index];

            System.arraycopy(tempBuffer, 0, sendBuffer, 0, index);

            return sendBuffer;
        }else{
            return new byte[0];
        }

    }

    public static byte[] listToByteArray(List<BaseVo> list){

        if(list == null || list.size()==0){
            return new byte[0];
        }

        int listSize = list.size();

        byte[] tempbuffer = new byte[1400];

        //总条数
        byte[] bytes = ByteUtil.intToBytes(listSize);

        int index = 0;

        //添加总数
        System.arraycopy(bytes, 0, tempbuffer, index, bytes.length);

        index += bytes.length;

        for(int i=0 ; i<listSize; i++){
            BaseVo baseVo = list.get(i);
            bytes = baseVoToByteArray(baseVo);

            //若buffer长度不够，需要增长
            int itemLength = bytes.length ;
            if ((tempbuffer.length - index - 1) < itemLength) {
                byte[] buffer;
                if(itemLength>1400){
                    buffer = new byte[tempbuffer.length + itemLength + 1400];
                }else{
                    buffer = new byte[tempbuffer.length + 1400];
                }

                System.arraycopy(tempbuffer, 0, buffer, 0, tempbuffer.length);
                tempbuffer = buffer;
            }

            System.arraycopy(bytes, 0, tempbuffer, index, bytes.length);
            index += bytes.length;
        }

        return tempbuffer;
    }

    private static Object[] decodeProperties(byte[] receiveBuffer, BaseVo dataVo, short[] dataTypes, int[] startIndex) {
        byte[] bytes;
        short dataType;
        Object property = null;
        Object[] properties = new Object[dataTypes.length];
        int index = startIndex[0];

        boolean debugDate = false;

        //根据属性的类型解码数据内容
        for (int i = 0; i < dataTypes.length; i++) {
            dataType = dataTypes[i];

            if (dataType == BaseVo.BYTE) {
                if(receiveBuffer.length-index>0) {
                    property = receiveBuffer[index++];
                }else {
                    property = (byte)0;
                }
//                if(debug && debugDate)System.out.println("hex BYTE : "+byte2HexString((Byte) property));
            } else if (dataType == BaseVo.SHORT) {
                bytes = new byte[2];
                if(receiveBuffer.length-index>0) {
                    System.arraycopy(receiveBuffer, index, bytes, 0, 2);
                }
//                if(debug && debugDate)System.out.println("hex SHORT : " + bytes2HexString(bytes));
                property = ByteUtil.shortFromBytes(bytes);
                index += bytes.length;
            } else if (dataType == BaseVo.INT) {
                bytes = new byte[4];

                if(receiveBuffer.length-index>0) {

                    if (receiveBuffer.length - index < 4) {

                        System.arraycopy(receiveBuffer, index, bytes, 0, receiveBuffer.length - index);

                    } else {
                        System.arraycopy(receiveBuffer, index, bytes, 0, 4);
                    }
                }
//                System.arraycopy(receiveBuffer, index, bytes, 0, 4);
//                if(debug && debugDate)System.out.println("hex INT : " + bytes2HexString(bytes));
                property = ByteUtil.intFromBytes(bytes);
                index += bytes.length;
            } else if (dataType == BaseVo.STRING) {

                if(receiveBuffer.length-index>0) {

                    int len = receiveBuffer[index++] & 0xFF;
                    bytes = new byte[len];
                    System.arraycopy(receiveBuffer, index, bytes, 0, len);
//                    if (debug && debugDate)System.out.println("STRING : int len = " + len + " hex string :" + bytes2HexString(bytes));

                    property = bytes;
                    index += len;

                }else{
                    property = new byte[1];
                }
            } else if (dataType == BaseVo.DATA) {
                if(receiveBuffer.length-index>0) {
                    int len = 0;
                    bytes = new byte[4];
                    if (receiveBuffer.length - index < 4) {
                        index += 4;
                    }else{
                        System.arraycopy(receiveBuffer, index, bytes, 0, 4);
                        len = ByteUtil.intFromBytes(bytes);
                        index += bytes.length;
                    }

                    bytes = new byte[len];
                    System.arraycopy(receiveBuffer, index, bytes, 0, len);
                    property = bytes;
                    index += len;
                }else{
                    property = new byte[0];
                }

            } else if (dataType == BaseVo.LIST) {

                if(dataVo==null)
                    continue;

                short[] elementDataTypes = dataVo.getElementDataTypes(i);
                byte listLength;
                //特殊处理0x0210协议，list中无声明长度，固定20长度

                listLength = (byte) properties[i - 1]; //这里是前面解析出来的数组的长度

//                if(debug && debugDate)System.out.println("LIST : hex len = " + byte2HexString(listLength));
                startIndex[0] = index;
                for (int j = 0; j < listLength; j++) {

                    dataVo.addListElement(decodeProperties(receiveBuffer, dataVo, elementDataTypes, startIndex), i);
                }
            }

            properties[i] = property;
        }

        startIndex[0] = index;

        return properties;
    }

    private static <T extends BaseVo> List<T> byteArrayToList(byte[] data, Class<T> baseVo){

        List<T> resultList = new ArrayList<>();

        int index = 0;

        int[] startIndex = {index};

        int listLength = getInt(data, index);

        if(listLength == 0){
            return resultList;
        }
        index += 4;

        startIndex[0] = index;

        for(int i = 0; i<listLength; i++){

            T item;
            try {
                item = baseVo.newInstance();
            } catch (Exception e) {
                e.printStackTrace();
                return resultList;
            }

            Object[] properties = decodeProperties(data, item, item.getDataTypes(), startIndex);

            item.setProperties(properties);

            resultList.add(item);

        }
        return resultList;
    }

    public static List<AttachmentItemVo> getAccessory(byte[] data){

        if(data == null || data.length == 0){
            return new ArrayList<>();
        }

        List<AttachmentItemVo> attachmentItemVos = byteArrayToList(data, AttachmentItemVo.class);

        return attachmentItemVos;

    }

    public static List<MaintenanceOrderProgress> getOrderProgress(byte[] data){

        if(data == null || data.length == 0){
            return new ArrayList<>();
        }

        List<MaintenanceOrderProgress> maintenanceOrderProgressList = byteArrayToList(data, MaintenanceOrderProgress.class);

        return maintenanceOrderProgressList;
    }

    public static int getOrderStatus(SelectOrderByNumberVo orderInfo){

        byte[] processList = orderInfo.getProcessList();
        if(processList != null && processList.length>0) {

            List<MaintenanceOrderProgress> orderProgress = DecodeUDPDataTool.getOrderProgress(processList);

            for(int i = 0; i<orderProgress.size(); i++){
                MaintenanceOrderProgress maintenanceOrderProgress = orderProgress.get(i);
                byte disposeResult = maintenanceOrderProgress.getDisposeResult();
                if(disposeResult == 0){
                    return getOrderStatus(orderInfo.getResultOrderName(),maintenanceOrderProgress);
                }
            }
            return MaintenanceOrderInfoBean.FINISH_STATUS;

        }

        return -1;

    }

    public static String getOrderStatusStr(SelectOrderByNumberVo orderInfo){

        byte[] processList = orderInfo.getProcessList();
        if(processList != null && processList.length>0) {

            List<MaintenanceOrderProgress> orderProgress = DecodeUDPDataTool.getOrderProgress(processList);

            for(int i = 0; i<orderProgress.size(); i++){
                MaintenanceOrderProgress maintenanceOrderProgress = orderProgress.get(i);
                byte disposeResult = maintenanceOrderProgress.getDisposeResult();
                if(disposeResult == 0){
                    return maintenanceOrderProgress.getProgressName();
                }
            }
            return "";

        }

        return "";

    }

    private static int getOrderStatus(String orderName, MaintenanceOrderProgress orderProgress){
        String progressName = orderProgress.getProgressName();
        if(orderName.equals(MainApplication.getInstance().getString(com.shinetech.mvp.R.string.order_name_maintenance))){
            String[] ordreMaintenanceProgress = MainApplication.getInstance().getResources().getStringArray(com.shinetech.mvp.R.array.ordre_maintenance_progress);
            for(int i = 0; i<ordreMaintenanceProgress.length; i++ ){
                if(progressName.equals(ordreMaintenanceProgress[i])){
                    switch (i){
                        case 0:
                        case 1:
                        case 2:
                            return MaintenanceOrderInfoBean.COMMIT_STATUS;
                        case 3:
                            return MaintenanceOrderInfoBean.PROCESSED_STATUS;
                        case 4:
                            return MaintenanceOrderInfoBean.EVALUATE_STATUS;
                    }
                }
            }


        }else if(orderName.equals(MainApplication.getInstance().getString(com.shinetech.mvp.R.string.order_name_install))){
            String[] ordreInstallProgress = MainApplication.getInstance().getResources().getStringArray(com.shinetech.mvp.R.array.ordre_install_progress);
            for(int i = 0; i<ordreInstallProgress.length; i++ ){
                if(progressName.equals(ordreInstallProgress[i])){
                    switch (i){
                        case 0:
                        case 1:
                        case 2:
                        case 3:
                        case 4:
                        case 5:
                            return MaintenanceOrderInfoBean.COMMIT_STATUS;
                        case 6:
                            return MaintenanceOrderInfoBean.PROCESSED_STATUS;
                        case 7:
                        case 8:
                            return MaintenanceOrderInfoBean.FINISH_STATUS;
                    }
                }
            }

        }

        return -1;
    }

}
