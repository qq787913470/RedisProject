package com.ypq.demo1.controller;

import com.ypq.demo1.Model.User;
import com.ypq.demo1.Service.UserService;
import com.ypq.demo1.redis.RedisConfig;
//import com.ypq.demo1.redis.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.RedisClusterConfiguration;
import org.springframework.data.redis.connection.RedisNode;
import org.springframework.web.bind.annotation.*;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisCluster;

import java.io.*;
import java.util.*;


@RestController
public class UserController {

    @Autowired
    private UserService userServiceImpl;
    @Autowired
    private RedisConfig redisConfig;
//    @Autowired
//    private RedisUtil redisUtil;

    /**
     * 获取一个用户，先从缓存中查找
     * @param id
     * @return
     * @throws ClassNotFoundException
     * @throws IllegalAccessException
     * @throws InstantiationException
     */
    @RequestMapping(value = "/userId/{id}", method = RequestMethod.GET)
    @ResponseBody
    public User findByUserId(@PathVariable("id") int id) throws ClassNotFoundException, IllegalAccessException, InstantiationException {
        User user = null;
        JedisCluster jedisCluster = getjedisCluster();
        if (jedisCluster.get(String.valueOf(id)) != null) {
            byte[] usertoString = jedisCluster.get(String.valueOf(id).getBytes());
            Object unserizlize = unserizlize(usertoString);
            if (unserizlize instanceof User) {
                return (User) unserizlize;
            }
        }
        if (user == null) {
            user = userServiceImpl.getUserById(id);
            jedisCluster.set(String.valueOf(id).getBytes(),serialize(user));
            return user;
        }
        return user;
    }
    /**
     * 获取所有的用户，存放于redis中
     * @return
     */
    @RequestMapping("/getAll")
    @ResponseBody
    public List<User> findAll(){
        JedisCluster jedisCluster = getjedisCluster();
        List<User> allUser = userServiceImpl.getAllUser();
        Object[] objects = allUser.toArray();
        for (int i = 0; i < objects.length; i++) {
            User objects1 = (User) objects[i];
            int id1 = objects1.getId();
            jedisCluster.set(String.valueOf(id1).getBytes(),serialize(objects1));
        }
        try {
            jedisCluster.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return allUser;
    }

    /**
     * 通过id删除，先删除数据库
     * @param id
     * @return
     */
    @RequestMapping("/delete/{id}")
    @ResponseBody
    public boolean delete(@PathVariable("id") int id) {
        User user = null;
        userServiceImpl.delete(id);
        JedisCluster jedisCluster = getjedisCluster();
        if (jedisCluster.get(String.valueOf(id)) != null) {
            byte[] usertoString = jedisCluster.get(String.valueOf(id).getBytes());
            jedisCluster.del(usertoString);
            return true;
        }else{
            return false;
        }
    }

    /**
     * 更新用户信息
     * @param u
     * @return
     */
    @RequestMapping(value = "/update",method = RequestMethod.POST)
    @ResponseBody
    public boolean update(@ModelAttribute  User u) {
        User user = null;
        userServiceImpl.update(u);
        int id = u.getId();
        JedisCluster jedisCluster = getjedisCluster();
        if (jedisCluster.get(String.valueOf(id)) != null) {
            byte[] usertoString = jedisCluster.get(String.valueOf(id).getBytes());
            jedisCluster.set(usertoString,serialize(u));
            return true;
        }else{
            return false;
        }
    }

    /**
     * 创建JedisCluster类对象
     * @return
     */
 public JedisCluster getjedisCluster(){
     RedisClusterConfiguration redisClusterConfiguration = redisConfig.redisClusterConfiguration();
     JedisCluster jedisCluster=null;
     Set<RedisNode> clusterNodes = redisClusterConfiguration.getClusterNodes();
     Iterator<RedisNode> iterator = clusterNodes.iterator();
     HashSet<HostAndPort> hostAndPorts  =new HashSet<HostAndPort>();
     if(iterator.hasNext()) {
         RedisNode redisNode = iterator.next();
         String host = redisNode.getHost();
         int post = redisNode.getPort();
         redis.clients.jedis.HostAndPort hostAndPort = new redis.clients.jedis.HostAndPort(host,post);
         hostAndPorts.add(hostAndPort);
     }
     jedisCluster = new JedisCluster(hostAndPorts);
     return jedisCluster;
 }

    //序列化
    public static byte [] serialize(Object obj){
        ObjectOutputStream obi=null;
        ByteArrayOutputStream bai=null;
        try {
            bai=new ByteArrayOutputStream();
            obi=new ObjectOutputStream(bai);
            obi.writeObject(obj);
            byte[] byt=bai.toByteArray();
            return byt;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    //反序列化
    public static Object unserizlize(byte[] byt){
        ObjectInputStream oii=null;
        ByteArrayInputStream bis=null;
        bis=new ByteArrayInputStream(byt);
        try {
            oii=new ObjectInputStream(bis);
            Object obj=oii.readObject();
            return obj;
        } catch (Exception e) {

            e.printStackTrace();
        }
        return null;
    }
}
