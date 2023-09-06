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
*   1.排班大一和大二两班(一周)
*
*   2.一个人一天只能值一班
*
*   3.大三一班(一周)
*
*   4.如果这个班能有大二的尽量有大二的，如果实在没有再去大三的里面找
*
*   5.如果实在不行大二的可以在同一个班
*
*   6.根据需求每一个班至少一个大二的，所以我们先排大二的，再进行大一的，大三的最后排
*        一、每一个大二的可以带多个大一的
*        二、先看大二的是否能分布开，分布不开，只能有多个大二的情况
*        三、排大一的班，可能会出现班排满了的情况，但是最后我们可以进行一个补漏的操作，让它这一班必须有人，哪怕是超过人数
*        四、如果出现这一班只有大一的情况，就去大三的里面找一个带他们，找不到就只能随机一个班，先看人数的优先
* */


@Service
public class CourseServiceImpl implements CourseService {
    @Resource
    private StaffMapper staffMapper;


    /*
    *   用来扫描Excel
    *   扫描课表
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
            // 初始化部门成员的ID 和 年级 ;
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
            *   从这可以入手看谁的的表格没有按要求填写
            *   System.out.println("这个课表人的姓名 : " + sheetName);
            * */

            List<String> strings = null;
            List<Integer> freeTime = null;
            List<String> sophomore = null;
            List<String> junior = null;
            System.out.println(" 正在扫描 : " + sheetName + " 的课表");
            for (int row = 3; row <= 7; row++) {
                int maxRol = sheet.getRow(row).getLastCellNum();
                for (int col = 2 ; col < maxRol; col++) {
                    if (col == 2 || col == 4 || col == 6 || col == 9 || col == 10 || col == 12 || col == 13) {
                        int workId = mappingDay.get(col);
                        workId += (row - 3 ) * 7 ;
                        // 跳过周末班
                        if( workId % 7 == 6 || workId % 7 == 5)continue;
                        // 获取这一行的信息
                        XSSFCell cell = sheet.getRow(row).getCell(col);
                        // 这一班没有课的人
                        if (cell == null || cell.toString().length() <= 1) {
                            // 没课对应的学生还没创建出来?
                            if(Course.noCourseStaffId.containsKey(workId)){
                                strings = Course.noCourseStaffId.get(workId);
                            }else{
                                strings = new ArrayList<String>();
                            }
                            // 单独用来存放大二的学生
                            if (Course.allPeopleInfo.get(sheetName).getGrade() == 2){
                                if(Course.Sophomore.containsKey(workId)){
                                    sophomore = Course.Sophomore.get(workId);
                                }else{
                                    sophomore = new ArrayList<>();
                                    Course.Sophomore.put(workId , sophomore);
                                }
                                sophomore.add(sheetName);
                            }
                            // 单独存放大三的学生
                            if (Course.allPeopleInfo.get(sheetName).getGrade() == 3){
                                if(Course.Junior.containsKey(workId)){
                                    junior = Course.Junior.get(workId);
                                }else{
                                    junior = new ArrayList<>();
                                    Course.Junior.put(workId , junior);
                                }
                                junior.add(sheetName);
                            }
                            // 这一班对应无课的学生
                            strings.add(sheetName);
                            Course.noCourseStaffId.put(workId , strings);
                            // 空闲时间表
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
*   这个算法更公平，但是时间复杂度更高，大二人超过 20 人时 ， 算的很慢 , 达到较高的指数级别
*
* */
    public void dfsSortCourseSophomore( int curworkId  , int curMax ){

        // 如果当前课表已经达到35说明已经安排完了 ， 也不能是周末
        if(curworkId >= 35 ){
            return;
        }
        if(curMax > max){
            // 清除原来的方案，放最新的方案
            everySophomoreWork.clear();
            everySophomoreWork.putAll(tmpEverySophomoreWork);
            max = curMax;
        }
        if(curworkId != 0 && (curworkId % 6 == 0 || curworkId % 5 == 0)){
            dfsSortCourseSophomore( ++curworkId , curMax);
            return;
        }
        // 从 curWork - 35 之间进行枚举，得到最好的结果
        // 第curworkId班选第 j 个学生的方案
        if(Course.Sophomore.get(curworkId) == null){
            dfsSortCourseSophomore(++curworkId , curMax);
            return;
        }
        for(int j = 0 ; j < Course.Sophomore.get(curworkId).size() ; j++){
            // 安排上，是否满班 && 是否今天有班 都满足条件才能进行安排人
            if(Utils.fullShift(Course.Sophomore.get(curworkId).get(j)) &&
                    checkTodayHasWork(Course.Sophomore.get(curworkId).get(j) , curworkId) &&
                    Utils.sameProfessionalClass(Course.Sophomore.get(curworkId).get(j) , curworkId)){
                // 安排进去 , 值班数减去 1
                tmpEverySophomoreWork.put(curworkId ,Course.Sophomore.get(curworkId).get(j) );
                Utils.theNumberOfShiftsMinusOne(Course.Sophomore.get(curworkId).get(j));

                dfsSortCourseSophomore( curworkId + 1 , curMax + 1);
            }
            // 如果你不值这一班 , 上一班可能已经安排进去了，得把当前信息移除 , 原本减少的班得加回去
            if(tmpEverySophomoreWork.containsKey(curworkId) && tmpEverySophomoreWork.get(curworkId).equals(Course.Sophomore.get(curworkId).get(j))){
                tmpEverySophomoreWork.remove(curworkId);
                Utils.theNumberOfShiftsUpOne(Course.Sophomore.get(curworkId).get(j));
            }
            dfsSortCourseSophomore( curworkId + 1,  curMax );
        }
    }
    /*
    *   采用一种比较不是很靠谱的安排方法
    * */
    public void planToCourseSphone(){
        // 1.首先要排某一个班人数比较少的，要先进行关照
        List<Map.Entry<Integer, List<String>>> sortedEntries = Course.Sophomore.entrySet()
                .stream()
                .sorted(Comparator.comparing(entry -> entry.getValue().size())).toList();

        for(Map.Entry<Integer , List<String>> entry : sortedEntries){

            int workId = entry.getKey();
            List<String> names = entry.getValue();

            // 进行洗牌 ， 打乱顺序性
            Utils.shuffleString(names);
            for(int i = 0 ; i < names.size() ; i++ ){
                // 没有安排满 ，且今天没有班 ，且没有相同班级的人
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
    *   编写一个临时判断今天是否已经安排值班的工具方法
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
*   开始对刚刚得到的最佳方案进行一个采纳
*       采纳这个方案，就要将这个方案的数据放到对应的值班那里去，并且每个人值班情况减去一
*
* */
    public void adoptionScenarios(){
        for(Map.Entry<Integer , String> entry : everySophomoreWork.entrySet()){
            // 1.给它安排这一班
            // 2.减少这个人值班数量
            Utils.addGetCourseMappingStaff(entry.getKey() , entry.getValue());

        }
        // 清空原本的情况
        tmpEverySophomoreWork.clear();
        everySophomoreWork.clear();
        max = 0 ;
    }


/*
*   大二的安排完了，剩余的空可能需要大三的来进行填满 , 因为大二实在抽不开人来进行培训大一的了
*
* */
    public  void dfsSortCourseJunior(int curWorkId , int curMax){
        if(curWorkId >= 35)return;
        if(curMax > max){
            // 清除原来的方案，放最新的方案
            everySophomoreWork.clear();
            everySophomoreWork.putAll(tmpEverySophomoreWork);
            max = curMax;
        }
        if(curWorkId % 7 == 5 || curWorkId % 7 == 6){
            dfsSortCourseJunior( ++curWorkId , curMax);
            return;
        }
        // 从 curWork - 35 之间进行枚举，得到最好的结果
        // 第curworkId班选第 j 个学生的方案
        if(!Course.Junior.containsKey(curWorkId)){
            dfsSortCourseJunior(++curWorkId , curMax);
            return;
        }
        for(int j = 0 ; j < Course.Junior.get(curWorkId).size() ; j++){
            // 这一班已经有人了
            if(Course.getCourseMappingStaff.get(curWorkId) != null && Course.getCourseMappingStaff.get(curWorkId).size() != 0){
                dfsSortCourseJunior(curWorkId + 1 , curMax);
            }else if(!Utils.morningAndEveningShifts(curWorkId)){
                // 如果走到这里说明这一班是早晚班
                dfsSortCourseJunior(curWorkId + 1 , curMax );
            }else{
                // 今天没有班且 ， 且 还有班可以值 且 没有同一个班的人
                if(Utils.fullShift(Course.Junior.get(curWorkId).get(j)) && checkTodayHasWork(Course.Junior.get(curWorkId).get(j) , curWorkId) && Utils.sameProfessionalClass(Course.Junior.get(curWorkId).get(j) , curWorkId)){
                    // 安排进去 , 值班数减去 1
                    tmpEverySophomoreWork.put(curWorkId ,Course.Junior.get(curWorkId).get(j) );
                    Utils.theNumberOfShiftsMinusOne(Course.Junior.get(curWorkId).get(j));
                    dfsSortCourseJunior( curWorkId + 1 , curMax + 1);
                }
                // 如果你不值这一班 , 上一班可能已经安排进去了，得把当前信息移除 , 原本减少的班得加回去
                if(tmpEverySophomoreWork.containsKey(curWorkId) && tmpEverySophomoreWork.get(curWorkId).equals(Course.Junior.get(curWorkId).get(j))){
                    tmpEverySophomoreWork.remove(curWorkId);
                    Utils.theNumberOfShiftsUpOne(Course.Junior.get(curWorkId).get(j));
                }
                dfsSortCourseJunior(curWorkId + 1 , curMax);
            }
        }
    }

/*
*   大三的安排完了，进行放进安排列表中
* */
public void scheduleJuniorYear(){
    for(Map.Entry<Integer , String> entry : everySophomoreWork.entrySet()){
        // 1.给它安排这一班
        // 2.减少这个人值班数量
        Utils.addGetCourseMappingStaff(entry.getKey() , entry.getValue());

    }
    // 清空原本的情况
    tmpEverySophomoreWork.clear();
    everySophomoreWork.clear();
    max = 0 ;
}

/*
*   现在课表已经最大限度的给每一班安排大三或者大二的，如果有的课表还没有大二或大三的，那就是真的没有了
* */

    public void scheduleEveryoneUp(){
        // 1.首先要排某一个班人数比较少的，要先进行关照
        List<Map.Entry<Integer, List<String>>> sortedEntries = Course.noCourseStaffId.entrySet()
                .stream()
                .sorted(Comparator.comparing(entry -> entry.getValue().size())).toList();

        List<String> everyWorkNames = null ;
        int workId = -1 ;
        for(Map.Entry<Integer, List<String>> everyWork : sortedEntries){
            workId = everyWork.getKey();
            everyWorkNames = everyWork.getValue();
            // 打乱这个无课名单
            Utils.shuffleString(everyWorkNames);
            // 原本已经添加的人已经添加上去了一部分大二或者大三的了，要减去那一班的人数 , 因为这一班不能安排太多人 ， 每一个班都是有限制的
            for(int i = 0 , j = 0 ; i < everyWorkNames.size() &&
                    j < Course.getWorkMappingStaffNumber.get(workId) -
                            Course.getCourseMappingStaff.getOrDefault(workId, Collections.emptyList()).size(); i++){
                // 大三的特判
                if(Course.allPeopleInfo.get(everyWorkNames.get(i)).getGrade() == 3){

                    if(!Utils.morningAndEveningShifts(workId))continue;
                    if(Utils.fullShift(everyWorkNames.get(i)) && Utils.checkTodayHasWork(everyWorkNames.get(i) , workId) &&
                            Utils.sameProfessionalClass(everyWorkNames.get(i) , workId)){
                        Utils.addGetCourseMappingStaff(workId , everyWorkNames.get(i));
                        j++;
                    }
                    continue;
                }

                // 如果没有课并且今天没有班并且这一班没有同班的人就可以安排班
                if(Utils.fullShift(everyWorkNames.get(i)) && Utils.checkTodayHasWork(everyWorkNames.get(i) , workId) &&
                    Utils.sameProfessionalClass(everyWorkNames.get(i) , workId)){
                    Utils.addGetCourseMappingStaff(workId , everyWorkNames.get(i));
                    j++;
                }
            }

        }
    }


/*
*   最后的排班，如果某一个班人数已经满了，最后就只能在他的值班中随机进行筛选一个进行安排进去，这里可能会出现一个班的人过多的情况
*
* */

    public void finalscheduleEveryoneUp(){
        // 遍历里面所有的人，看还有谁没有班安排完
        Staff staff = null;
        String name = null;
        for(Map.Entry<String , Staff> remainStaff : Course.allPeopleInfo.entrySet()){
            name = remainStaff.getKey();
            staff = remainStaff.getValue();
            // 如果满班了直接跳过
            if(!Utils.fullShift(staff.getName()))continue;
            Utils.shuffleNumber(staff.getFreeTime());
            for(int i = 0 ; i < staff.getFreeTime().size() ; i++ ){
                // 如果已经排满班就不安排了
                if( !Utils.fullShift(name))break;
                // 先安排班没安排满的人
                if(Course.getWorkMappingStaffNumber.get(staff.getFreeTime().get(i)) - Course.getCourseMappingStaff.get(staff.getFreeTime().get(i)).size() <= 0)continue;
                // 如果是大三的，且是早晚班直接跳过
                if(Course.allPeopleInfo.get(name).getGrade() == 3 && !Utils.morningAndEveningShifts(staff.getFreeTime().get(i)))continue;
                // 今天是否有班 && 是否有相同班级的人
                if(Utils.fullShift(name) && Utils.checkTodayHasWork(name , staff.getFreeTime().get(i)) && Utils.sameProfessionalClass(name , staff.getFreeTime().get(i))){
                    Utils.addGetCourseMappingStaff(staff.getFreeTime().get(i) , name);
                }
            }
            // 如果还没安排满，说明每一个班都是满人的 ， 只能随机插到满人班中去了
            if(!Utils.fullShift(name))continue;
            Random random = new Random();
            while(Utils.fullShift(staff.getName())){

                if(Course.allPeopleInfo.get(name).getGrade() == 3){
                    int randomWorkId = random.nextInt(staff.getFreeTime().size());
                    // 检查是不是早晚班
                    if(!Utils.morningAndEveningShifts(staff.getFreeTime().get(randomWorkId)))continue;
                    // 今天是否有班 && 是否有相同班级的人
                    if(Utils.checkTodayHasWork(name , staff.getFreeTime().get(randomWorkId)) && Utils.sameProfessionalClass(name , staff.getFreeTime().get(randomWorkId))){
                        Utils.addGetCourseMappingStaff(staff.getFreeTime().get(randomWorkId) , name);
                    }
                    continue;
                }

                int randomWorkId = random.nextInt(staff.getFreeTime().size());
                // 今天是否有班 && 是否有相同班级的人
                if(Utils.checkTodayHasWork(name , staff.getFreeTime().get(randomWorkId)) && Utils.sameProfessionalClass(name , staff.getFreeTime().get(randomWorkId))){
                    Utils.addGetCourseMappingStaff(staff.getFreeTime().get(randomWorkId) , name);
                }
            }
        }
    }

    /*
    *   将结果显示出来
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

            System.out.println( "星期 " + (workId % 7 + 1 ) + " 第 " + (workId / 7 + 1 ) + " 班值班的有 : " + sb);
        }

    }

    /*
    *   检查是不是所有人已经安排完了 , 如果显示的不是 '已经全部安排完了' ， 说明有人的时间太紧张了，需要手动进行
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
            System.out.println( " 已经全部安排完了 ");
        }


    }
    @PostConstruct
    private void initStaff(){
        for(int i = 0 , j = 5 , k = 6  ; i < 35 ; i++ , j += 7 , k += 7){
            // 每一班初始化是4个人
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
