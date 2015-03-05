package com.myelin.site.careme.test;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.myelin.site.careme.api.dao.ibatis.MyTestDao;
import com.myelin.site.careme.api.dao.jpa.MyJpaDAO;

@ContextConfiguration(locations = {"classpath*:db.xml","classpath:home/dal/db-jpa.xml","classpath:home/dal/dao.xml"})
@RunWith(SpringJUnit4ClassRunner.class)
@ActiveProfiles("spring-data-jpa")
public class CareMeTest {
	
	@Autowired
	MyTestDao testdao;
	
	@Autowired
	MyJpaDAO jpadao;
	
	@Test
	public void testFindOne() {
		System.out.println(testdao.getBasicInfo());
		System.out.println(jpadao.findAll());
	}
}
