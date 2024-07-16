package com.example.service;

import cn.hutool.core.date.DateUnit;
import cn.hutool.core.util.IdUtil;
import com.example.common.enums.OrderStatusEnum;
import com.example.common.enums.RecordsEnum;
import com.example.common.enums.ResultCodeEnum;
import com.example.entity.*;
import com.example.exception.CustomException;
import com.example.mapper.OrdersMapper;
import com.example.utils.TokenUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.kennycason.kumo.CollisionMode;
import com.kennycason.kumo.WordCloud;
import com.kennycason.kumo.bg.CircleBackground;
import com.kennycason.kumo.font.KumoFont;
import com.kennycason.kumo.font.scale.LinearFontScalar;
import com.kennycason.kumo.font.scale.LogFontScalar;
import com.kennycason.kumo.font.scale.SqrtFontScalar;
import com.kennycason.kumo.palette.ColorPalette;
import org.apache.commons.collections4.ComparatorUtils;
import org.apache.commons.collections4.comparators.ComparableComparator;
import org.springframework.stereotype.Service;
import com.kennycason.kumo.WordFrequency;
import com.kennycason.kumo.nlp.FrequencyAnalyzer;
import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.math.*;
import javax.annotation.Resource;
import javax.imageio.ImageIO;
import java.util.List;
import java.util.Date;
import java.time.LocalDateTime;
import cn.hutool.core.date.DateUtil;
import java.util.*;
import java.util.stream.Collectors;
import org.apache.commons.collections4.comparators.ComparatorChain;
import com.huaban.analysis.jieba.JiebaSegmenter;
import com.huaban.analysis.jieba.SegToken;
import org.springframework.transaction.annotation.Transactional;

/**
 * 订单信息业务处理
 **/
@Service
public class OrdersService {

    @Resource
    private OrdersMapper ordersMapper;

    @Resource
    private UserService userService;

    @Resource
    private  AddressService addressService;

    @Resource
    private  CommentService commentService;

    @Resource
    private CertificationService certificationService;

    /**
     * 新增
     */
    public void add(Orders orders) {
        ordersMapper.insert(orders);
    }

    /**
     * 删除
     */
    public void deleteById(Integer id) {
        ordersMapper.deleteById(id);
    }

    /**
     * 批量删除
     */
    public void deleteBatch(List<Integer> ids) {
        for (Integer id : ids) {
            ordersMapper.deleteById(id);
        }
    }

    /**
     * 修改
     */
//    public void updateById(Orders orders) {
//
//        ordersMapper.updateById(orders);
//    }

    @Transactional
     public void updateById(Orders orders) {
        if ("待收货".equals(orders.getStatus())) {
            // 骑手送达了订单
            //  打钱
            Integer acceptId = orders.getAcceptId();
            User user = userService.selectById(acceptId);
            user.setAccount(user.getAccount().add(BigDecimal.valueOf(orders.getPrice())));
            userService.updateById(user);
            // 记录收支明细
            RecordsService.addRecord("接单-" + orders.getName(), BigDecimal.valueOf(orders.getPrice()), RecordsEnum.INCOME.getValue());
        }
        ordersMapper.updateById(orders);
    }
    /**
     * 根据ID查询
     */
    /*public Orders selectById(Integer id) {
        return ordersMapper.selectById(id);
    }*/
    public Orders selectById(Integer id) {
        Orders orders = ordersMapper.selectById(id);
        Address address = addressService.selectById(orders.getAddressId());
        orders.setAddress(address);  // 取货地址
        Address targetAddress = addressService.selectById(orders.getTargetId());
        orders.setTargetAddress(targetAddress);  // 收货地址

        Certification certification=certificationService.selectById(orders.getAcceptId());
        orders.setCertification(certification);
        List<Comment>  comment=commentService.selectById(orders.getAcceptId());
        orders.setCommentc(comment);



        return orders;
    }
    /**
     * 查询所有
     */

    public Orders wordcloud(Integer id) {
        Orders orders = ordersMapper.selectById(id);

        Certification certification = certificationService.selectById(orders.getAcceptId());
        orders.setCertification(certification);
        List<Comment> comment = commentService.selectById(orders.getAcceptId());

        List<String> contentList = comment.stream()
                .map(Comment::getContent)
                .collect(Collectors.toList());
            System.out.println(contentList);
        // 生成词云图
        byte[] wordcloudImage = generateWordCloudImage(contentList);
        String wordcloudImageUrl = saveWordcloudImage(wordcloudImage);

        orders.setWordcloudImage(wordcloudImage);

        return orders;
    }
    private byte[] generateWordCloudImage(List<String> contentList) {
        // 使用中文分词库（jieba）进行分词
        JiebaSegmenter segmenter = new JiebaSegmenter();

        // 创建词云图像大小和碰撞模式
        Dimension dimension = new Dimension(600, 600);  // 增大图像尺寸以获得更好的可视化效果
        WordCloud wordCloud = new WordCloud(dimension, CollisionMode.PIXEL_PERFECT);


        java.awt.Font font=new java.awt.Font("STSong-Light",2,40);
        // 设置词云图像的背景颜色为白色
        wordCloud.setBackgroundColor(new Color(255, 242, 193, 200)); // 带透明度的粉红色
        wordCloud.setBackground(new CircleBackground(255));
        wordCloud.setFontScalar(new LinearFontScalar(10, 120));
        /*wordCloud.setKumoFont(new KumoFont(font));*/
        wordCloud.setKumoFont(new KumoFont(new Font("SimSun", Font.PLAIN, 18)));

        // 设置颜色调色板，使每个词都有不同的颜色
        wordCloud.setColorPalette(new ColorPalette(Color.RED, Color.BLUE, Color.GREEN, Color.CYAN, Color.ORANGE));
        String stopWords="骑手";
        // 设置字体

        // 分词并加载词频
        Map<String, Integer> wordFrequencyMap = new HashMap<>();
        for (String content : contentList) {
            List<SegToken> segTokens = segmenter.process(content, JiebaSegmenter.SegMode.INDEX);
            for (SegToken segToken : segTokens) {
                String word = segToken.word;
                if (!stopWords.contains(word)) {
                wordFrequencyMap.put(word, wordFrequencyMap.getOrDefault(word, 0) + 1);}
            }
        }
        // 构建词云
        List<WordFrequency> wordFrequencies = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : wordFrequencyMap.entrySet()) {
            wordFrequencies.add(new WordFrequency(entry.getKey(), entry.getValue()));
        }

        // 设置词云中字体大小与词频相关
        wordCloud.setPadding(5); // 增加间距以改善词云中的词语分离效果
        wordCloud.build(wordFrequencies);

        // 生成词云图像
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        wordCloud.writeToStreamAsPNG(outputStream);
        return outputStream.toByteArray();
    }
   /* private byte[] generateWordCloudImage(List<String> contentList) {
        // 使用中文分词库（jieba）进行分词
        JiebaSegmenter segmenter = new JiebaSegmenter();

        // 创建词云图像大小和碰撞模式
        Dimension dimension = new Dimension(500, 500);
        WordCloud wordCloud = new WordCloud(dimension, CollisionMode.PIXEL_PERFECT);

        // 设置词云图像的背景颜色为白色
        wordCloud.setBackgroundColor(new Color(230, 230, 230));

        // 设置颜色调色板，使每个词都有不同的颜色
        wordCloud.setColorPalette(new ColorPalette(Color.RED, Color.BLUE, Color.GREEN, Color.CYAN, Color.ORANGE));

        // 设置字体
        wordCloud.setKumoFont(new KumoFont(new Font("SimSun", Font.PLAIN, 40)));

        // 分词并加载词频
        List<WordFrequency> wordFrequencies = new ArrayList<>();
        Set<String> uniqueWords = new HashSet<>();
        for (String content : contentList) {
            List<SegToken> segTokens = segmenter.process(content, JiebaSegmenter.SegMode.INDEX);
            for (SegToken segToken : segTokens) {
                String word = segToken.word;
                if (!uniqueWords.contains(word)) {
                    wordFrequencies.add(new WordFrequency(word, word.length()));
                    uniqueWords.add(word);
                }
            }
        }

        // 构建词云
        wordCloud.build(wordFrequencies);

        // 生成词云图像
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        wordCloud.writeToStreamAsPNG(outputStream);
        return outputStream.toByteArray();
    }*/

/*    // 构建词云
        wordCloud.build(wordFrequencies);

        // 过滤掉重复词和低频词
        List<WordFrequency> filteredFrequencies = wordFrequencies.stream()
                .filter(wordFrequency -> wordFrequency.getFrequency() > 1)  // 调整低频词的阈值
                .distinct()
                .collect(Collectors.toList());

        // 构建词云
        wordCloud.build(filteredFrequencies);

        // 生成词云图像
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        wordCloud.writeToStreamAsPNG(outputStream);
        return outputStream.toByteArray();
    }*/



/*   private byte[] generateWordCloudImage(List<String> contentList) {
        FrequencyAnalyzer frequencyAnalyzer = new FrequencyAnalyzer();
        Dimension dimension = new Dimension(500, 500);
        WordCloud wordCloud = new WordCloud(dimension, CollisionMode.PIXEL_PERFECT);
        wordCloud.setPadding(2);
       Point point = new Point(100, 100);
       wordCloud.setBackgroundColor(new Color(255, 100, 255)); // 设置背景颜色为白色

        wordCloud.setColorPalette(new ColorPalette(new Color(0x001F3F), new Color(0x003366), new Color(0x005C99)));
       wordCloud.setKumoFont(new KumoFont(new Font("SimSun", Font.PLAIN, 10)));

        List<WordFrequency> wordFrequencies = frequencyAnalyzer.load(contentList);
        wordCloud.build(wordFrequencies);

        // 生成词云图像
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        wordCloud.writeToStreamAsPNG(outputStream);
        return outputStream.toByteArray();
    }*/

    private String saveWordcloudImage(byte[] wordcloudImage) {
        try {
            // 实现将词云图保存到文件或云存储的逻辑
            // 这里简单演示保存到文件，你可以根据实际情况保存到云存储等
            String filePath = "D://c/wordcloud.png";
            File file = new File(filePath);
            org.apache.commons.io.FileUtils.writeByteArrayToFile(file, wordcloudImage);

            // 返回保存图像的URL，这里返回本地文件的路径作为示例
            return file.getAbsolutePath();
        } catch (IOException e) {
            // 处理异常，例如日志记录或抛出自定义异常
            e.printStackTrace();
            return null;
        }
    }



//    public List<Orders> selectAll(Orders orders) {
//        return ordersMapper.selectAll(orders);
//    }
    public List<Orders> selectAll(Orders orders) {
        List<Orders> ordersList = ordersMapper.selectAll(orders);
        for (Orders o : ordersList) {
            String time = o.getTime();
            Date date = new Date();
            int range = (int) DateUtil.between(DateUtil.parseDateTime(time), date, DateUnit.MINUTE);
            o.setRange(range);
        }

        return ordersList;
    }

    /**
     * 分页查询
     */
    public PageInfo<Orders> selectPage(Orders orders, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<Orders> list = ordersMapper.selectAll(orders);
        return PageInfo.of(list);
    }

    public void addOrder(Orders orders) {
        Account currentUser = TokenUtils.getCurrentUser();
        System.out.println("sss:"+currentUser);
        BigDecimal account = currentUser.getAccount();
        Double price = orders.getPrice();
        if (price > account.doubleValue()) {

            throw new CustomException(ResultCodeEnum.ACCOUNT_LIMIT_ERROR);
        }
        // 更新账户余额
        currentUser.setAccount(account.subtract(BigDecimal.valueOf(price)));
        System.out.println("sss:"+currentUser);
        userService.updateById((User) currentUser);

        orders.setUserId(currentUser.getId());
        orders.setOrderNo(IdUtil.getSnowflakeNextIdStr());  // 设置唯一的订单编号
        orders.setStatus(OrderStatusEnum.NO_ACCEPT.getValue());
        orders.setTime(DateUtil.now());
        ordersMapper.insert(orders);
        // 记录收支明细
        RecordsService.addRecord("下单-" + orders.getName(), BigDecimal.valueOf(orders.getPrice()), RecordsEnum.OUT.getValue());

    }

    public void accept(Orders orders) {
        Account currentUser = TokenUtils.getCurrentUser();  // 骑手用户
        orders.setAcceptId(currentUser.getId());
        orders.setAcceptTime(DateUtil.now());
        orders.setStatus(OrderStatusEnum.NO_ARRIVE.getValue());
        this.updateById(orders);
    }

}