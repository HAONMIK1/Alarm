package alarm.Dao;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import alarm.model.AlertDTO;

@Component("AlertDao")
public class AlertDao {
	@Autowired
	SqlSessionTemplate sqlSessionTemplate;
	

	public void getInsertAlert(AlertDTO ad) {
		sqlSessionTemplate.insert("alarm.AlertDTO.insertAlert",ad);
		
		
	}
}
