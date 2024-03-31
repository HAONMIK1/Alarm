package alarm.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import alarm.Dao.AlertDao;
import alarm.model.AlertDTO;
import java.io.*;
import java.net.*;
/**
 * Handles requests for the application home page.
 */
@Controller
public class HomeController {
	
	private static final Logger logger = LoggerFactory.getLogger(HomeController.class);
	
	/**
	 * Simply selects the home view to render by returning its name.
	 */
	@Autowired
	private AlertDao dao;
	
	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String home(Locale locale, Model model) throws Exception {
		logger.info("Welcome home! The client locale is {}.", locale);
		
		Date date = new Date();
		DateFormat dateFormat = DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.LONG, locale);
		
		String formattedDate = dateFormat.format(date);
		
		model.addAttribute("serverTime", formattedDate );
		BufferedReader br = new BufferedReader(new InputStreamReader(new ClassPathResource("a.csv").getInputStream()));
        String line;
        int mcount4 =0;// 지속시간
        int mcount2 =0;
        int ccount3 =0;// 지속시간
        int ccount1 =0;
        int mGrade =0;
        int cGrade=0;
        boolean skipHeader = true; // 첫 번째 줄이 헤더인지 여부를 나타내는 변수
        int pm10=0;
        int pm25=0;
        int time=0;
        while ((line = br.readLine()) != null) {
            if (skipHeader) {
                skipHeader = false;
                continue; // 헤더는 건너뛰고 다음 줄로 이동
            }

            List<String> aLine = new ArrayList<String>();

            String[] lineArr = line.split(",", -1);
            aLine = Arrays.asList(lineArr);
            String[] day =lineArr[0].split(" ");
           if(day[1].equals("01")) {
        	   time=0;
           }
            time++;
          

            //시간당 평균 농도 데이터 추출
            if(!aLine.get(3).isEmpty()||!aLine.get(4).isEmpty()){
                 pm10 =(pm10+Integer.parseInt(aLine.get(3)))/time;
                 pm25 = (pm25+Integer.parseInt(aLine.get(4)))/time;
                 System.out.println(day[1]+time+"pm10 :"+pm10+"pm2.5 :"+pm25);
            }else {
            	 System.out.println("측정소(구별): " + aLine.get(1) + ", 경보 단계: 측정소 점검 , 발령 시간: " + aLine.get(0));
                 AlertDTO ad= new AlertDTO();
                 ad.setLocation(aLine.get(1));
                 ad.setAlert_check("측정소 점검");
                 ad.setTime( aLine.get(0));
                 dao.getInsertAlert(ad);
                 continue;
            }
          
                
            
            
            if (pm10 >= 150&&pm10 < 300) {  // 시간당 평균 농도가 150㎍/㎥ 이상인 경우
                mcount4++; // 연속 시간 증가
                mcount2 =0;
            }else if(pm10 >= 300){// 시간당 평균 농도가 300㎍/㎥ 이상인 경우
                mcount2++;
                mcount4++;
            }else{
                mcount4 =0;
                mcount2 =0;

            }
            
            
            if(pm25 >= 75&&pm25 < 150){// 시간당 평균 농도가 75㎍/㎥ 이상인 경우
                ccount3++;
                ccount1 =0;
            }else if(pm25 >= 150){// 시간당 평균 농도가 150㎍/㎥ 이상인 경우
                ccount1++;
                ccount3++;
            }
            else{
                 ccount3 =0;
                 ccount1 =0;
            }

            // 연속 시간이 2시간 이상인 경우 주의보 발령
            if (mcount4 >= 2 && ccount3 <2 && ccount1 <2 && mcount2 <2) {
                mGrade=4;
                System.out.println("측정소(구별): " + aLine.get(1) + ", 경보 단계: " + mGrade  + ", 발령 시간: " + aLine.get(0)+time);
                AlertDTO ad= new AlertDTO();
                ad.setLocation(aLine.get(1));
                ad.setGrade(mGrade);
                ad.setTime( aLine.get(0));
                ad.setAlert_check("");
                dao.getInsertAlert(ad);
            }
           else if (mcount2 >= 2 && ccount1<2) {
                mGrade=2;
                System.out.println("측정소(구별): " + aLine.get(1) + ", 경보 단계: " + mGrade  + ", 발령 시간: " + aLine.get(0) +time);
                AlertDTO ad= new AlertDTO();
                ad.setLocation(aLine.get(1));
                ad.setGrade(mGrade);
                ad.setTime( aLine.get(0));
                ad.setAlert_check("");
                dao.getInsertAlert(ad);
            }

           else if (ccount3 >= 2 && mcount2 < 2 && ccount1 < 2) {
                cGrade=3;
                System.out.println("측정소(구별): " + aLine.get(1) + ", 경보 단계: " + cGrade  + ", 발령 시간: " + aLine.get(0)+time);
                AlertDTO ad= new AlertDTO();
                ad.setLocation(aLine.get(1));
                ad.setGrade(cGrade);
                ad.setTime( aLine.get(0));
                ad.setAlert_check("");
                dao.getInsertAlert(ad);
            }
          else if (ccount1 >= 2) {
                cGrade=1;
               System.out.println("측정소(구별): " + aLine.get(1) + ", 경보 단계: " + cGrade  + ", 발령 시간: " + aLine.get(0)+time);
               AlertDTO ad= new AlertDTO();
                ad.setLocation(aLine.get(1));
                ad.setGrade(cGrade);
                ad.setTime( aLine.get(0));
                ad.setAlert_check("");
                dao.getInsertAlert(ad);
           }
          else {
              continue;
            }
        }
		return "home";
	}
	
	
	

}
