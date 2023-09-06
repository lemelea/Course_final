package com.tang.course.service.impl;



import com.tang.course.commons.Course;
import com.tang.course.mapper.StaffMapper;
import com.tang.course.pojo.Staff;
import com.tang.course.service.CourseService;
import com.tang.course.utils.Utils;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;

/*
*   1.�Ű��һ�ʹ������(һ��)
*
*   2.һ����һ��ֻ��ֵһ��
*
*   3.����һ��(һ��)
*
*   4.�����������д���ľ����д���ģ����ʵ��û����ȥ������������
*
*   5.���ʵ�ڲ��д���Ŀ�����ͬһ����
*
*   6.��������ÿһ��������һ������ģ������������Ŵ���ģ��ٽ��д�һ�ģ������������
*        һ��ÿһ������Ŀ��Դ������һ��
*        �����ȿ�������Ƿ��ֲܷ������ֲ�������ֻ���ж����������
*        �����Ŵ�һ�İ࣬���ܻ���ְ������˵����������������ǿ��Խ���һ����©�Ĳ�����������һ��������ˣ������ǳ�������
*        �ġ����������һ��ֻ�д�һ���������ȥ������������һ�������ǣ��Ҳ�����ֻ�����һ���࣬�ȿ�����������
* */


@Service
public class CourseServiceImpl implements CourseService {
    @Resource
    private StaffMapper staffMapper;


    /*
    *   ����ɨ��Excel
    *   ɨ��α�
    * */
    public void scanFiles(String path) throws IOException {
        XSSFWorkbook xssfWorkbook = new XSSFWorkbook(new FileInputStream(path));
        int sheetNum = xssfWorkbook.getNumberOfSheets();
        HashMap<Integer , Integer> mappingDay = initDayMapping();
        for(int i = 0;i<sheetNum - 1 ;i++) {
            XSSFSheet sheet = xssfWorkbook.getSheetAt(i);
            String sheetName = sheet.getSheetName();
            Course.allPeopleInfo.put(sheetName , new Staff());
            Staff staff  = Course.allPeopleInfo.get(sheetName);
            // ��ʼ�����ų�Ա��ID �� �꼶 ;
            staff.setFreeTime(new ArrayList<Integer>());
            staff.setWorkId(new ArrayList<>());
            Staff selectOne = staffMapper.selectByName(sheetName);
            staff.setCountWork(selectOne.getCountWork());
            staff.setId(selectOne.getId());
            staff.setSubject(selectOne.getSubject());
            staff.setGrade(selectOne.getGrade());
            staff.setName(selectOne.getName());
            /*
            *
            *   ����������ֿ�˭�ĵı��û�а�Ҫ����д
            *   System.out.println("����α��˵����� : " + sheetName);
            * */

            List<String> strings = null;
            List<Integer> freeTime = null;
            List<String> sophomore = null;
            List<String> junior = null;
            System.out.println(" ����ɨ�� : " + sheetName + " �Ŀα�");
            for (int row = 3; row <= 7; row++) {
                int maxRol = sheet.getRow(row).getLastCellNum();
                for (int col = 2 ; col < maxRol; col++) {
                    if (col == 2 || col == 4 || col == 6 || col == 9 || col == 10 || col == 12 || col == 13) {
                        int workId = mappingDay.get(col);
                        workId += (row - 3 ) * 7 ;
                        // ������ĩ��
                        if( workId % 7 == 6 || workId % 7 == 5)continue;
                        // ��ȡ��һ�е���Ϣ
                        XSSFCell cell = sheet.getRow(row).getCell(col);
                        // ��һ��û�пε���
                        if (cell == null || cell.toString().length() <= 1) {
                            // û�ζ�Ӧ��ѧ����û��������?
                            if(Course.noCourseStaffId.containsKey(workId)){
                                strings = Course.noCourseStaffId.get(workId);
                            }else{
                                strings = new ArrayList<String>();
                            }
                            // ����������Ŵ����ѧ��
                            if (Course.allPeopleInfo.get(sheetName).getGrade() == 2){
                                if(Course.Sophomore.containsKey(workId)){
                                    sophomore = Course.Sophomore.get(workId);
                                }else{
                                    sophomore = new ArrayList<>();
                                    Course.Sophomore.put(workId , sophomore);
                                }
                                sophomore.add(sheetName);
                            }
                            // ������Ŵ�����ѧ��
                            if (Course.allPeopleInfo.get(sheetName).getGrade() == 3){
                                if(Course.Junior.containsKey(workId)){
                                    junior = Course.Junior.get(workId);
                                }else{
                                    junior = new ArrayList<>();
                                    Course.Junior.put(workId , junior);
                                }
                                junior.add(sheetName);
                            }
                            // ��һ���Ӧ�޿ε�ѧ��
                            strings.add(sheetName);
                            Course.noCourseStaffId.put(workId , strings);
                            // ����ʱ���
                            freeTime = Course.allPeopleInfo.get(sheetName).getFreeTime();
                            freeTime.add(workId);
                        }
                    }
                }
            }
        }
        xssfWorkbook.close();

    }

    private int max = 0 ;

    private HashMap<Integer , String> tmpEverySophomoreWork = new HashMap<>();
    private HashMap<Integer , String> everySophomoreWork = new HashMap<>();
/*
*   ����㷨����ƽ������ʱ�临�Ӷȸ��ߣ�����˳��� 20 ��ʱ �� ��ĺ��� , �ﵽ�ϸߵ�ָ������
*
* */
    public void dfsSortCourseSophomore( int curworkId  , int curMax ){

        // �����ǰ�α��Ѿ��ﵽ35˵���Ѿ��������� �� Ҳ��������ĩ
        if(curworkId >= 35 ){
            return;
        }
        if(curMax > max){
            // ���ԭ���ķ����������µķ���
            everySophomoreWork.clear();
            everySophomoreWork.putAll(tmpEverySophomoreWork);
            max = curMax;
        }
        if(curworkId != 0 && (curworkId % 6 == 0 || curworkId % 5 == 0)){
            dfsSortCourseSophomore( ++curworkId , curMax);
            return;
        }
        // �� curWork - 35 ֮�����ö�٣��õ���õĽ��
        // ��curworkId��ѡ�� j ��ѧ���ķ���
        if(Course.Sophomore.get(curworkId) == null){
            dfsSortCourseSophomore(++curworkId , curMax);
            return;
        }
        for(int j = 0 ; j < Course.Sophomore.get(curworkId).size() ; j++){
            // �����ϣ��Ƿ����� && �Ƿ�����а� �������������ܽ��а�����
            if(Utils.fullShift(Course.Sophomore.get(curworkId).get(j)) &&
                    checkTodayHasWork(Course.Sophomore.get(curworkId).get(j) , curworkId) &&
                    Utils.sameProfessionalClass(Course.Sophomore.get(curworkId).get(j) , curworkId)){
                // ���Ž�ȥ , ֵ������ȥ 1
                tmpEverySophomoreWork.put(curworkId ,Course.Sophomore.get(curworkId).get(j) );
                Utils.theNumberOfShiftsMinusOne(Course.Sophomore.get(curworkId).get(j));

                dfsSortCourseSophomore( curworkId + 1 , curMax + 1);
            }
            // ����㲻ֵ��һ�� , ��һ������Ѿ����Ž�ȥ�ˣ��ðѵ�ǰ��Ϣ�Ƴ� , ԭ�����ٵİ�üӻ�ȥ
            if(tmpEverySophomoreWork.containsKey(curworkId) && tmpEverySophomoreWork.get(curworkId).equals(Course.Sophomore.get(curworkId).get(j))){
                tmpEverySophomoreWork.remove(curworkId);
                Utils.theNumberOfShiftsUpOne(Course.Sophomore.get(curworkId).get(j));
            }
            dfsSortCourseSophomore( curworkId + 1,  curMax );
        }
    }
    /*
    *   ����һ�ֱȽϲ��Ǻܿ��׵İ��ŷ���
    * */
    public void planToCourseSphone(){
        // 1.����Ҫ��ĳһ���������Ƚ��ٵģ�Ҫ�Ƚ��й���
        List<Map.Entry<Integer, List<String>>> sortedEntries = Course.Sophomore.entrySet()
                .stream()
                .sorted(Comparator.comparing(entry -> entry.getValue().size())).toList();

        for(Map.Entry<Integer , List<String>> entry : sortedEntries){

            int workId = entry.getKey();
            List<String> names = entry.getValue();

            // ����ϴ�� �� ����˳����
            Utils.shuffleString(names);
            for(int i = 0 ; i < names.size() ; i++ ){
                // û�а����� ���ҽ���û�а� ����û����ͬ�༶����
                if(Utils.fullShift(names.get(i)) && checkTodayHasWork(names.get(i) , workId) && Utils.sameProfessionalClass(names.get(i), workId)){
                    tmpEverySophomoreWork.put(workId , names.get(i));
                    Utils.theNumberOfShiftsMinusOne(names.get(i));
                    break;
                }

            }

        }
        everySophomoreWork.putAll(tmpEverySophomoreWork);
    }

    /*
    *   ��дһ����ʱ�жϽ����Ƿ��Ѿ�����ֵ��Ĺ��߷���
    * */
    public boolean checkTodayHasWork(String name , int curWorkId){
        if(Objects.isNull(name) || name.length() == 0)return false;
        int dayOfTheWeek = Math.abs( curWorkId % 7) ;
        for(int i = dayOfTheWeek ; i < curWorkId ; i += 7 ){
            if(tmpEverySophomoreWork.containsKey(i) && tmpEverySophomoreWork.get(i).equals(name)){
                return false;
            }
        }
        return true;
    }


/*
*
*   ��ʼ�Ըոյõ�����ѷ�������һ������
*       ���������������Ҫ��������������ݷŵ���Ӧ��ֵ������ȥ������ÿ����ֵ�������ȥһ
*
* */
    public void adoptionScenarios(){
        for(Map.Entry<Integer , String> entry : everySophomoreWork.entrySet()){
            // 1.����������һ��
            // 2.���������ֵ������
            Utils.addGetCourseMappingStaff(entry.getKey() , entry.getValue());

        }
        // ���ԭ�������
        tmpEverySophomoreWork.clear();
        everySophomoreWork.clear();
        max = 0 ;
    }


/*
*   ����İ������ˣ�ʣ��Ŀտ�����Ҫ���������������� , ��Ϊ���ʵ�ڳ鲻������������ѵ��һ����
*
* */
    public  void dfsSortCourseJunior(int curWorkId , int curMax){
        if(curWorkId >= 35)return;
        if(curMax > max){
            // ���ԭ���ķ����������µķ���
            everySophomoreWork.clear();
            everySophomoreWork.putAll(tmpEverySophomoreWork);
            max = curMax;
        }
        if(curWorkId % 7 == 5 || curWorkId % 7 == 6){
            dfsSortCourseJunior( ++curWorkId , curMax);
            return;
        }
        // �� curWork - 35 ֮�����ö�٣��õ���õĽ��
        // ��curworkId��ѡ�� j ��ѧ���ķ���
        if(!Course.Junior.containsKey(curWorkId)){
            dfsSortCourseJunior(++curWorkId , curMax);
            return;
        }
        for(int j = 0 ; j < Course.Junior.get(curWorkId).size() ; j++){
            // ��һ���Ѿ�������
            if(Course.getCourseMappingStaff.get(curWorkId) != null && Course.getCourseMappingStaff.get(curWorkId).size() != 0){
                dfsSortCourseJunior(curWorkId + 1 , curMax);
            }else if(!Utils.morningAndEveningShifts(curWorkId)){
                // ����ߵ�����˵����һ���������
                dfsSortCourseJunior(curWorkId + 1 , curMax );
            }else{
                // ����û�а��� �� �� ���а����ֵ �� û��ͬһ�������
                if(Utils.fullShift(Course.Junior.get(curWorkId).get(j)) && checkTodayHasWork(Course.Junior.get(curWorkId).get(j) , curWorkId) && Utils.sameProfessionalClass(Course.Junior.get(curWorkId).get(j) , curWorkId)){
                    // ���Ž�ȥ , ֵ������ȥ 1
                    tmpEverySophomoreWork.put(curWorkId ,Course.Junior.get(curWorkId).get(j) );
                    Utils.theNumberOfShiftsMinusOne(Course.Junior.get(curWorkId).get(j));
                    dfsSortCourseJunior( curWorkId + 1 , curMax + 1);
                }
                // ����㲻ֵ��һ�� , ��һ������Ѿ����Ž�ȥ�ˣ��ðѵ�ǰ��Ϣ�Ƴ� , ԭ�����ٵİ�üӻ�ȥ
                if(tmpEverySophomoreWork.containsKey(curWorkId) && tmpEverySophomoreWork.get(curWorkId).equals(Course.Junior.get(curWorkId).get(j))){
                    tmpEverySophomoreWork.remove(curWorkId);
                    Utils.theNumberOfShiftsUpOne(Course.Junior.get(curWorkId).get(j));
                }
                dfsSortCourseJunior(curWorkId + 1 , curMax);
            }
        }
    }

/*
*   �����İ������ˣ����зŽ������б���
* */
public void scheduleJuniorYear(){
    for(Map.Entry<Integer , String> entry : everySophomoreWork.entrySet()){
        // 1.����������һ��
        // 2.���������ֵ������
        Utils.addGetCourseMappingStaff(entry.getKey() , entry.getValue());

    }
    // ���ԭ�������
    tmpEverySophomoreWork.clear();
    everySophomoreWork.clear();
    max = 0 ;
}

/*
*   ���ڿα��Ѿ�����޶ȵĸ�ÿһ�ల�Ŵ������ߴ���ģ�����еĿα�û�д��������ģ��Ǿ������û����
* */

    public void scheduleEveryoneUp(){
        // 1.����Ҫ��ĳһ���������Ƚ��ٵģ�Ҫ�Ƚ��й���
        List<Map.Entry<Integer, List<String>>> sortedEntries = Course.noCourseStaffId.entrySet()
                .stream()
                .sorted(Comparator.comparing(entry -> entry.getValue().size())).toList();

        List<String> everyWorkNames = null ;
        int workId = -1 ;
        for(Map.Entry<Integer, List<String>> everyWork : sortedEntries){
            workId = everyWork.getKey();
            everyWorkNames = everyWork.getValue();
            // ��������޿�����
            Utils.shuffleString(everyWorkNames);
            // ԭ���Ѿ���ӵ����Ѿ������ȥ��һ���ִ�����ߴ������ˣ�Ҫ��ȥ��һ������� , ��Ϊ��һ�಻�ܰ���̫���� �� ÿһ���඼�������Ƶ�
            for(int i = 0 , j = 0 ; i < everyWorkNames.size() &&
                    j < Course.getWorkMappingStaffNumber.get(workId) -
                            Course.getCourseMappingStaff.getOrDefault(workId, Collections.emptyList()).size(); i++){
                // ����������
                if(Course.allPeopleInfo.get(everyWorkNames.get(i)).getGrade() == 3){

                    if(!Utils.morningAndEveningShifts(workId))continue;
                    if(Utils.fullShift(everyWorkNames.get(i)) && Utils.checkTodayHasWork(everyWorkNames.get(i) , workId) &&
                            Utils.sameProfessionalClass(everyWorkNames.get(i) , workId)){
                        Utils.addGetCourseMappingStaff(workId , everyWorkNames.get(i));
                        j++;
                    }
                    continue;
                }

                // ���û�пβ��ҽ���û�аಢ����һ��û��ͬ����˾Ϳ��԰��Ű�
                if(Utils.fullShift(everyWorkNames.get(i)) && Utils.checkTodayHasWork(everyWorkNames.get(i) , workId) &&
                    Utils.sameProfessionalClass(everyWorkNames.get(i) , workId)){
                    Utils.addGetCourseMappingStaff(workId , everyWorkNames.get(i));
                    j++;
                }
            }

        }
    }


/*
*   �����Ű࣬���ĳһ���������Ѿ����ˣ�����ֻ��������ֵ�����������ɸѡһ�����а��Ž�ȥ��������ܻ����һ������˹�������
*
* */

    public void finalscheduleEveryoneUp(){
        // �����������е��ˣ�������˭û�аల����
        Staff staff = null;
        String name = null;
        for(Map.Entry<String , Staff> remainStaff : Course.allPeopleInfo.entrySet()){
            name = remainStaff.getKey();
            staff = remainStaff.getValue();
            // ���������ֱ������
            if(!Utils.fullShift(staff.getName()))continue;
            Utils.shuffleNumber(staff.getFreeTime());
            for(int i = 0 ; i < staff.getFreeTime().size() ; i++ ){
                // ����Ѿ�������Ͳ�������
                if( !Utils.fullShift(name))break;
                // �Ȱ��Ű�û����������
                if(Course.getWorkMappingStaffNumber.get(staff.getFreeTime().get(i)) - Course.getCourseMappingStaff.get(staff.getFreeTime().get(i)).size() <= 0)continue;
                // ����Ǵ����ģ����������ֱ������
                if(Course.allPeopleInfo.get(name).getGrade() == 3 && !Utils.morningAndEveningShifts(staff.getFreeTime().get(i)))continue;
                // �����Ƿ��а� && �Ƿ�����ͬ�༶����
                if(Utils.fullShift(name) && Utils.checkTodayHasWork(name , staff.getFreeTime().get(i)) && Utils.sameProfessionalClass(name , staff.getFreeTime().get(i))){
                    Utils.addGetCourseMappingStaff(staff.getFreeTime().get(i) , name);
                }
            }
            // �����û��������˵��ÿһ���඼�����˵� �� ֻ������嵽���˰���ȥ��
            if(!Utils.fullShift(name))continue;
            Random random = new Random();
            while(Utils.fullShift(staff.getName())){

                if(Course.allPeopleInfo.get(name).getGrade() == 3){
                    int randomWorkId = random.nextInt(staff.getFreeTime().size());
                    // ����ǲ��������
                    if(!Utils.morningAndEveningShifts(staff.getFreeTime().get(randomWorkId)))continue;
                    // �����Ƿ��а� && �Ƿ�����ͬ�༶����
                    if(Utils.checkTodayHasWork(name , staff.getFreeTime().get(randomWorkId)) && Utils.sameProfessionalClass(name , staff.getFreeTime().get(randomWorkId))){
                        Utils.addGetCourseMappingStaff(staff.getFreeTime().get(randomWorkId) , name);
                    }
                    continue;
                }

                int randomWorkId = random.nextInt(staff.getFreeTime().size());
                // �����Ƿ��а� && �Ƿ�����ͬ�༶����
                if(Utils.checkTodayHasWork(name , staff.getFreeTime().get(randomWorkId)) && Utils.sameProfessionalClass(name , staff.getFreeTime().get(randomWorkId))){
                    Utils.addGetCourseMappingStaff(staff.getFreeTime().get(randomWorkId) , name);
                }
            }
        }
    }

    /*
    *   �������ʾ����
    * */
    public void displayResult(){

        for(Map.Entry<Integer , List<String>> entry : Course.getCourseMappingStaff.entrySet()){
            int workId = entry.getKey();
            List<String> names = entry.getValue();

            StringBuilder sb = new StringBuilder();

            for(int i = 0 ; i < names.size() ; i++ ){
                sb.append(names.get(i));
                if(i != names.size() - 1 ){
                    sb.append(' ');
                }
            }

            System.out.println( "���� " + (workId % 7 + 1 ) + " �� " + (workId / 7 + 1 ) + " ��ֵ����� : " + sb);
        }

    }

    /*
    *   ����ǲ����������Ѿ��������� , �����ʾ�Ĳ��� '�Ѿ�ȫ����������' �� ˵�����˵�ʱ��̫�����ˣ���Ҫ�ֶ�����
    * */

    public void displaysInformationThatIsNotScheduledLeft(){

        boolean isflag = true;
        List<String> names = new ArrayList<>();
        for(Map.Entry<String , Staff> entry : Course.allPeopleInfo.entrySet()){

            if(Utils.fullShift(entry.getKey())){
                isflag = false;
                names.add(entry.getKey());
            }

        }
        if(!isflag){
            System.out.println(names);
        }else{
            System.out.println( " �Ѿ�ȫ���������� ");
        }


    }
    @PostConstruct
    private void initStaff(){
        for(int i = 0 , j = 5 , k = 6  ; i < 35 ; i++ , j += 7 , k += 7){
            // ÿһ���ʼ����4����
            if(i % 7 == 5 || i % 7 == 6 )continue;
            Course.getWorkMappingStaffNumber.put( i , 4 );

        }
    }
    private  HashMap<Integer , Integer> initDayMapping(){
        HashMap<Integer , Integer> map = new HashMap<Integer, Integer>();
        map.put(2 , 0);
        map.put(4 , 1);
        map.put(6 , 2);
        map.put(9 , 3);
        map.put(10 , 4);
        map.put(12 , 5);
        map.put(13 , 6);
        return map;
    }

}
