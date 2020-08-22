package com.nicholas.mybatis.plus.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.nicholas.mybatis.plus.entity.User;

public interface UserService extends IService<User> {

    void saveEntity();


    /**
     */
    void mysqlRepeatedTestOne(long id, long time);

    void mysqlRepeatedTestTwo();


    /**
     * mysql 临键锁 测试
     * 总结：mysql InnoDB通过临键锁额外锁住下一个左开右闭的空间解决幻读
     */
    void mysqlLinjianLockTest(long id, long time);


    /**
     * 现在有两个方法A，方法B，都带有Spring @Transactional的事务开启注解，假设方法A的事务记为A，方法B的事务记为B
     * 情形一：先开启事务A，然后id=n的数据，为null，然后开启B事务，插入id=n的数据并提交B事务，然后A中再查一次为null，相同的条件重复读取得到的结果是一样的，update同理
     * 情形二：先开启事务A，后开启事务B插入或者修改id=n的数据（或修改数据）并提交事务B，然后在事务A中能查询id=n的数据，update同理，可以查询到修改后的数据
     * 这是为什么呢，按照我们的理解，方法A开启事务后，在方法B中插入或修改数据库，A中应该是查询不到的
     * 然后在情形二中，在B中插入或修改id=n的数据之前，在A中先查询id=m的数据，最后结论跟情形一一样，初步怀疑@Transactional的方法中，只有在于数据库交互后才会开启事务
     * @param id
     * @param time
     */
    void mysqlTransactionTest(long id, long time);

    void mysqlTransactionTest2(long id);


    /**
     * 事实证明，先开启事务A，然后开启事务B，B修改id=n的数据并且提交事务，提交成功，然后A修改id=n，A提交事务也成功，A后提交，覆盖了B
     * @param id
     * @param time
     */
    void mysqlUpdateTest(long id, long time);

    void mysqlUpdateTest2(long id);

}
