package com.example.demo;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.demo.mapper.UserMapper;
import com.example.demo.pojo.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.*;

@SpringBootTest
class DemoApplicationTests {
    @Autowired
    private UserMapper userMapper;
    // 自动装填
    @Test
    public void testSelect() {
        System.out.println(("----- selectAll method test ------"));
        List<User> userList = null;
        try {
            userList = userMapper.selectList(null);
            userList.forEach(System.out::println);
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    @Test
    public void testInsert(){
        System.out.println(("----- insert method test ------"));
        for(int i = 0;i<5;++i){
            User user = new User(
                    String.format("test%02d",i),
                    5+i,
                    String.format("test%02d@email.com",i)
            );

            // id会回填
            System.out.println(userMapper.insert(user));
            System.out.println(user);
        }
    }

    @Test
    public void testUpdate(){
        System.out.println(("----- update method test ------"));
        testSelect();
        User user = new User();
        user.setId(1L);
        user.setName("testUpdate02");
        userMapper.updateById(user);
        System.out.println("modified part:" + user);
        System.out.println("In database:" + userMapper.selectById(1L));
        // 只update我传递的user里面有提供的部分，相当于override
    }
    @Test
    void testOptimisticLocker() {
        // origin: 100
        User user1 = userMapper.selectById(3L);
        System.out.println("User1:" + user1.getAge());

        User user2 = userMapper.selectById(3L);
        System.out.println("User2:" + user2.getAge());

        user1.setAge(user1.getAge() + 50);             // submit  100 + 50 = 150
        userMapper.updateById(user1);                  // successfully

        user2.setAge(user2.getAge() - 30);            // submit 100 - 30 = 70
        int result = userMapper.updateById(user2);    // newVersion != oldVersion, failed
        if(result == 0){
            System.out.println("User2 failed to update");
            user2 = userMapper.selectById(3L);
            user2.setAge(user2.getAge() - 30);
            userMapper.updateById(user2);
        }

        User user3 = userMapper.selectById(3L);
        System.out.println("finally result:" + user3.getAge());
    }
    @Test
    public void testOptLocker(){
        // thread-1
        User user1 = userMapper.selectById(1L);
        user1.setName("first");
        user1.setAge(111);

        // thread-2
        User user2 = userMapper.selectById(1L);
        user2.setName("second");
        user2.setAge(222);

        // 插队现场
        userMapper.updateById(user2);    // version 1 -> version 2
        userMapper.updateById(user1);    // version 1 != version 2  ---> failed
    }
    @Test
    public void testSelectBatchIds(){
        List<User> users = userMapper.selectBatchIds(Arrays.asList(1,2,3,4,5));
        users.forEach(System.out::println);
    }

    @Test
    public void testSelectByMap(){
        Map<String,Object> map = new HashMap<>();
        map.put("age", 9);
        List<User> users = userMapper.selectByMap(map);
        users.forEach(System.out::println);
    }
    @Test
    public void testQueryWrapper(){
        QueryWrapper<User> qw = new QueryWrapper<>();
        qw.gt("age",10);
        List<User> userArrayList= new ArrayList<>();
        userArrayList = userMapper.selectList(qw);
        userArrayList.forEach(System.out::println);
    }

    @Test
    public void testPaginationInterceptor(){
        Page<User> page = new Page<>(2,4);
        userMapper.selectPage(page,null);
        page.getRecords().forEach(System.out::println);

        //page的其他方法
        System.out.println("当前页：" + page.getCurrent());
        System.out.println("总页数：" + page.getPages());
        System.out.println("记录数：" + page.getTotal());
        System.out.println("是否有上一页：" + page.hasPrevious());
        System.out.println("是否有下一页：" + page.hasNext());
    }

    @Test
    public void testDeletById(){
        System.out.println("-------- logic deleted --------");
        userMapper.deleteById(3L);

        System.out.println("-------- logic search ---------");
        userMapper.selectById(3L);
    }
    @Test
    public void testWrapperLike(){
        QueryWrapper<User> qw = new QueryWrapper<>();
        qw.likeRight("name","test").likeLeft("name","1");
        userMapper.selectList(qw).forEach(System.out::println);
    }
    @Test
    public void testWrapperInSql(){
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.inSql("age","select age from user where age > 10");
        userMapper.selectList(wrapper).forEach(System.out::println);
    }
}
