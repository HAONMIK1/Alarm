package alarm.controller;

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
	PrintWriter pw =null;
	Socket socket =null;
	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String home(Locale locale, Model model) throws Exception {
		logger.info("Welcome home! The client locale is {}.", locale);
		
		Date date = new Date();
		DateFormat dateFormat = DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.LONG, locale);
		
		String formattedDate = dateFormat.format(date);
		
		model.addAttribute("serverTime", formattedDate );
	
		try {
			socket=new Socket("localhost",5555);
			pw=new PrintWriter(socket.getOutputStream());
		
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
        while ((line = br.readLine()) != null) {
            if (skipHeader) {
                skipHeader = false;
                continue; // 헤더는 건너뛰고 다음 줄로 이동
            }

            List<String> aLine = new ArrayList<String>();

            String[] lineArr = line.split(",", -1);
            aLine = Arrays.asList(lineArr);
            String[] day =lineArr[0].split(" ");
            int time =Integer.parseInt(day[1]);
            
            if (!aLine.get(3).isEmpty() && !aLine.get(4).isEmpty()) {
                pm10 += Integer.parseInt(aLine.get(3));
                pm25 += Integer.parseInt(aLine.get(4));
                pm10 /= time;
                pm25 /= time;
            } else {
               send(aLine.get(1),0,aLine.get(0),"측정소 점검");
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
                send(aLine.get(1),mGrade,aLine.get(0),"");
            }
           else if (mcount2 >= 2 && ccount1<2) {
                mGrade=2;
                send(aLine.get(1),mGrade,aLine.get(0),"");
               
            }

           else if (ccount3 >= 2 && mcount2 < 2 && ccount1 < 2) {
                cGrade=3;
                send(aLine.get(1),cGrade,aLine.get(0),"");
              
            }
          else if (ccount1 >= 2) {
                cGrade=1;
                send(aLine.get(1),cGrade,aLine.get(0),"");
            
          }
          else {
              continue;
            }
        }
   
      
		}catch (Exception e) {
			// TODO: handle exception
		}finally {
            // 소켓과 PrintWriter 닫기
            if (pw != null) {
                pw.close();
            }
            if (socket != null) {
                socket.close();
            }
        }
	     System.out.println("끝");
		return "home";
	}
	public void send(String location, int grade, String time, String check) throws Exception{
		
		String mess="측정소(구별): "+location+ ", 경보 단계: " +grade+ ", 발령 시간: " +time+" "+check;
      	pw.println(mess);
      	pw.flush();
      	
		AlertDTO ad= new AlertDTO();
          ad.setLocation(location);
          ad.setGrade(grade);
          ad.setTime(time);
          ad.setAlert_check(check);
          dao.getInsertAlert(ad);
		 
	}

	
	

}
