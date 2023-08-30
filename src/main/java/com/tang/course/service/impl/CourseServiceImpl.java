package com.tang.course.service.impl;



import com.tang.course.commons.Course;
import com.tang.course.mapper.StaffMapper;
import com.tang.course.pojo.Staff;
import com.tang.course.service.CourseService;
import com.tang.course.utils.Utils;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import java.io.FileInputStream;
import java.io.FileOutputStream;
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
    public void scanFiles() throws IOException {
        XSSFWorkbook xssfWorkbook = new XSSFWorkbook(new FileInputStream("C:\\Users\\Administrator\\Desktop\\mode.xlsx"));
        int sheetNum = xssfWorkbook.getNumberOfSheets();
        HashMap<Integer , Integer> mappingDay = initDayMapping();
        HashMap<String, Staff> allPeopleInfo = Course.allPeopleInfo;;
        for(int i = 0;i<sheetNum - 1 ;i++) {
            XSSFSheet sheet = xssfWorkbook.getSheetAt(i);
            String sheetName = sheet.getSheetName();
            Course.allPeopleInfo.put(sheetName , new Staff());
            Staff staff  = Course.allPeopleInfo.get(sheetName);
            // ��ʼ�����ų�Ա��ID �� �꼶 ;
            staff.setFreeTime(new ArrayList<Integer>());
            staff.setWorkId(new ArrayList<>());
//            System.out.println(sheetName);
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
            for (int row = 3; row <= 7; row++) {
                int maxRol = sheet.getRow(row).getLastCellNum();
                for (int col = 2 ; col < maxRol; col++) {
                    if (col == 2 || col == 4 || col == 6 || col == 9 || col == 10 || col == 12 || col == 13) {
                        int workId = mappingDay.get(col);
                        workId += (row - 3 ) * 7 ;
                        // ������ĩ��
                        if( workId != 0 && (workId % 7 == 6 || workId % 7 == 5))continue;
                        // ��ȡ��һ�е���Ϣ
                        XSSFCell cell = sheet.getRow(row).getCell(col);
                        // ��һ��û�пε���
                        if (cell == null || cell.toString().length() == 0) {
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
//                            System.out.println(Course.allPeopleInfo.get(sheetName));
                            freeTime = Course.allPeopleInfo.get(sheetName).getFreeTime();
                            freeTime.add(workId);
                        }
                    }
                }
            }
        }
        xssfWorkbook.close();
//        HashMap<Integer, List<String>> noCourseStaffId = Course.noCourseStaffId;
//
//        for(Map.Entry<Integer , List<String>> entry : noCourseStaffId.entrySet()){
//            System.out.print( " �� " + (entry.getKey() % 7 + 1 ) + " �� " + (entry.getKey() / 7 + 1 ) + "�� �� �޿�ѧ���� ");
//            for(String name : entry.getValue()){
//                System.out.print(" " + name);
//            }
//            System.out.println();
//        }

        // �Ѿ�ͳ����ɿα� ����1 - 5

//        System.out.println("������ !!!!!!!!!! ");
    }

    private int max = 0 ;

    private HashMap<Integer , String> tmpEverySophomoreWork = new HashMap<>();
    private HashMap<Integer , String> everySophomoreWork = new HashMap<>();

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
        if(curWorkId != 0 && (curWorkId % 6 == 0 || curWorkId % 5 == 0)){
            dfsSortCourseJunior( ++curWorkId , curMax);
            return;
        }
        // �� curWork - 35 ֮�����ö�٣��õ���õĽ��
        // ��curworkId��ѡ�� j ��ѧ���ķ���
        if(Course.Junior.get(curWorkId) == null){
            dfsSortCourseJunior(++curWorkId , curMax);
            return;
        }
        for(int j = 0 ; j < Course.Junior.get(curWorkId).size() ; j++){
            if(Course.getCourseMappingStaff.get(curWorkId) != null && Course.getCourseMappingStaff.get(curWorkId).size() != 0){
                dfsSortCourseJunior(curWorkId + 1 , curMax);
            }else{
                // ����û�а��� �� �� ���а����ֵ
                if(Utils.fullShift(Course.Junior.get(curWorkId).get(j)) && checkTodayHasWork(Course.Junior.get(curWorkId).get(j) , curWorkId)){
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
                // �����Ƿ��а� && �Ƿ�����ͬ�༶����
                if(Utils.checkTodayHasWork(name , staff.getFreeTime().get(i)) && Utils.sameProfessionalClass(name , staff.getFreeTime().get(i))){
                    Utils.addGetCourseMappingStaff(staff.getFreeTime().get(i) , name);
                }
            }
            // �����û��������˵��ÿһ���඼�����˵� �� ֻ������嵽���˰���ȥ��
            Random random = new Random();
            while(Utils.fullShift(staff.getName())){
                int randomWorkId = random.nextInt(staff.getFreeTime().size());
                // �����Ƿ��а� && �Ƿ�����ͬ�༶����
                if(Utils.checkTodayHasWork(name , staff.getFreeTime().get(randomWorkId)) && Utils.sameProfessionalClass(name , staff.getFreeTime().get(randomWorkId))){
                    Utils.addGetCourseMappingStaff(staff.getFreeTime().get(randomWorkId) , name);
                }
            }
        }
    }

    /*
    *   �����д��Excel�����
    * */
    public void writeForExcelAsResult() throws IOException {

        String writeFileName = "C:\\Users\\Administrator\\Desktop\\ֵ���.xlsx";
        FileInputStream fis = new FileInputStream(writeFileName);

        //1.����һ��������
        Workbook workbook = new XSSFWorkbook(fis);

        //��ȡ������
        Sheet sheet1 = workbook.getSheet("Sheet1");


        for(Map.Entry<Integer , List<String>> entry : Course.getCourseMappingStaff.entrySet()){
            int workId = entry.getKey();

            if(workId % 7 == 5 || workId % 7 == 6)continue;

            int row = workId / 7 ;
            int col = workId % 7 ;
            //ͨ�����±�����±귵��cell����getRow�б�,getCell�б�
            Cell cell =  sheet1.getRow(row).getCell(col);

            cell.setCellValue(entry.getValue().toString().substring(1 , entry.getValue().toString().length() - 1 ));

            FileOutputStream fos = new FileOutputStream(writeFileName);

            workbook.write(fos);

            fos.close();
        }


        //д��
        fis.close();

    }
    @PostConstruct
    private void initStaff(){
        List<Staff> staffs = staffMapper.selectList();


        for(int i = 0 , j = 5 , k = 6  ; i < 35 ; i++ , j += 7 , k += 7){
            // ÿһ���ʼ���������

            if(i % 7 == 5 || i % 7 == 6 )continue;
            Course.getWorkMappingStaffNumber.put( i , 5 );

//            if( j <= 33 )Course.weekDay.put(j , 4 );
//            if( k <= 34 )Course.weekDay.put(k , 4 );
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
