package com.example.service;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.date.DateUtil;
import com.example.entity.Account;
import com.example.entity.Comment;
import com.example.entity.Orders;
import com.example.entity.Records;
import com.example.mapper.RecordsMapper;
import com.example.utils.TokenUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import javax.annotation.Resource;
import javax.imageio.ImageIO;
import javax.swing.*;

import com.sun.org.apache.bcel.internal.generic.ARETURN;
import com.sun.org.apache.xerces.internal.dom.PSVIAttrNSImpl;
import javafx.scene.chart.XYChart;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.chart.renderer.xy.XYBarRenderer;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.statistics.HistogramDataset;
import org.jfree.data.statistics.HistogramType;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Year;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;
import org.jfree.chart.ChartUtils;

/**
 * 收支明细业务处理
 **/
@Service
public class RecordsService implements InitializingBean {

    @Resource
    private RecordsMapper recordsMapper;

    private static RecordsMapper staticRecordMapper;


    /**
     * 新增
     */
    public void add(Records records) {
        recordsMapper.insert(records);
    }

    /**
     * 删除
     */
    public void deleteById(Integer id) {
        recordsMapper.deleteById(id);
    }

    /**
     * 批量删除
     */
    public void deleteBatch(List<Integer> ids) {
        for (Integer id : ids) {
            recordsMapper.deleteById(id);
        }
    }

    /**
     * 修改
     */
    public void updateById(Records records) {
        recordsMapper.updateById(records);
    }

    /**
     * 根据ID查询
     */
    public Records selectById(Integer id) {
        return recordsMapper.selectById(id);
    }

    /**
     * 查询所有
     */
    public List<Records> selectAll(Records records) {
        return recordsMapper.selectAll(records);
    }

    /**
     * 分页查询
     */
    public PageInfo<Records> selectPage(Records records, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<Records> list = recordsMapper.selectAll(records);
        return PageInfo.of(list);
    }

    /**
     * 收支明细存储
     */
    public static void addRecord(String content, BigDecimal money, String type) {
        Records records = new Records();
        Account currentUser = TokenUtils.getCurrentUser();
        records.setUserId(currentUser.getId());
        records.setTime(DateUtil.now());
        records.setContent(content);
        records.setMoney(money);
        records.setType(type);
        staticRecordMapper.insert(records);  // 插入数据库
    }
//静态变量赋值
    @Override
    public void afterPropertiesSet() throws Exception {
        staticRecordMapper = recordsMapper;
    }


    public Records analysis(Integer id,String param2) {
        int number = Integer.parseInt(param2);
        List<Records> recordsList = recordsMapper.select2ById(id);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        List<LocalDateTime> timeList = recordsList.stream()
                .map(record -> LocalDateTime.parse(record.getTime(), formatter))
                .collect(Collectors.toList());
        List<BigDecimal> moneyList = recordsList.stream()
                .map(Records::getMoney)
                .collect(Collectors.toList());

        Map<LocalDateTime, Double> timeMoneyMap = new HashMap<>();
        for (int i = 0; i < timeList.size(); i++) {
            timeMoneyMap.put(timeList.get(i), moneyList.get(i).doubleValue());
        }

        Map<LocalDateTime, Double> sortedMap = timeMoneyMap.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                        (oldValue, newValue) -> oldValue, LinkedHashMap::new));
        int count = 0;

        LocalDate currentDate1 = null;
        int j = 0;
        List<Map.Entry<LocalDateTime, Double>> entries = new ArrayList<>(sortedMap.entrySet());
        Map<LocalDateTime, Double> truncatedMap = new LinkedHashMap<>();
        for (int i = entries.size() - 1; i > -100; i--) {
            count++;
            Map.Entry<LocalDateTime, Double> entry1 = entries.get(i);
            LocalDateTime dateTime1 = entry1.getKey();
            LocalDate date1 = dateTime1.toLocalDate();
            double money1 = entry1.getValue();
            if (currentDate1 == null || currentDate1.equals(date1)) {
                if (currentDate1 != null) {
                    truncatedMap.put(dateTime1, money1);
                } else {
                    truncatedMap.put(dateTime1, money1);
                    currentDate1 = date1;
                    j++;
                }
            } else {
                truncatedMap.put(dateTime1, money1);
                currentDate1 = date1;
                j++;
                if (j == number) {
                    break;
                }
            }
        }

        List<Map<LocalDate, Double>> dailyMoneyList = new ArrayList<>();
        Map<LocalDate, Double> dailyMoneyMap = new LinkedHashMap<>();
        LocalDate currentDate = null;
        double dailyTotalMoney = 0;

        for (Map.Entry<LocalDateTime, Double> entry : truncatedMap.entrySet()) {
            LocalDateTime dateTime = entry.getKey();
            double money = entry.getValue();
            LocalDate date = dateTime.toLocalDate();

            if (currentDate == null || !currentDate.equals(date)) {
                if (currentDate != null) {
                    dailyMoneyMap.put(currentDate, dailyTotalMoney);
                }
                currentDate = date;
                dailyTotalMoney = money;
            } else {
                dailyTotalMoney += money;
            }
        }

        if (currentDate != null) {
            dailyMoneyMap.put(currentDate, dailyTotalMoney);
        }

        List<Map.Entry<LocalDate, Double>> myList = new ArrayList<>(dailyMoneyMap.entrySet());
        Collections.reverse(myList);
        Map<LocalDate, Double> myMap = new LinkedHashMap<>();
        for (Map.Entry<LocalDate, Double> entry : myList) {
            myMap.put(entry.getKey(), entry.getValue());
        }

        double total = dailyMoneyMap.values().stream()
                .mapToDouble(Double::doubleValue)
                .sum();

        dailyMoneyList.add(myMap);

        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        for (Map<LocalDate, Double> record : dailyMoneyList) {
            for (Map.Entry<LocalDate, Double> entry : record.entrySet()) {
                dataset.addValue(entry.getValue(), "Income", entry.getKey().toString());
            }
        }

        JFreeChart chart = ChartFactory.createLineChart(
                "日收入变化趋势",
                "日期",
                "收入 (¥)",
                dataset,
                PlotOrientation.VERTICAL,
                true,
                true,
                false
        );

        chart.setBackgroundPaint(new Color(255, 255, 200, 0)); // 背景透明
        chart.getTitle().setPaint(Color.DARK_GRAY); // 标题颜色
        chart.getTitle().setFont(new Font("SansSerif", Font.BOLD, 24));

        CategoryPlot plot = (CategoryPlot) chart.getPlot();
        plot.setBackgroundPaint(Color.WHITE); // 背景色
        plot.setDomainGridlinePaint(Color.GRAY); // 网格线颜色
        plot.setRangeGridlinePaint(Color.GRAY);

        CategoryAxis domainAxis = plot.getDomainAxis();
        domainAxis.setTickLabelPaint(Color.DARK_GRAY); // X轴标签颜色
        domainAxis.setTickLabelFont(new Font("SansSerif", Font.PLAIN, 15)); // X轴标签字体
        domainAxis.setLabelFont(new Font("SansSerif", Font.BOLD, 24)); // X轴标题字体

        NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
        rangeAxis.setTickLabelPaint(Color.blue); // Y轴标签颜色
        rangeAxis.setTickLabelFont(new Font("SansSerif", Font.PLAIN, 20)); // Y轴标签字体
        rangeAxis.setLabelFont(new Font("SansSerif", Font.BOLD, 24)); // Y轴标题字体

        LineAndShapeRenderer renderer = (LineAndShapeRenderer) plot.getRenderer();
        renderer.setSeriesPaint(0, new Color(0, 102, 255)); // 折线颜色
        renderer.setSeriesStroke(0, new BasicStroke(2.0f)); // 折线粗细
        renderer.setSeriesShapesVisible(0, true); // 显示数据点
        renderer.setSeriesShape(0, new Ellipse2D.Double(-3, -3, 6, 6)); // 数据点形状
        renderer.setSeriesOutlinePaint(0, Color.WHITE); // 数据点边框颜色
        renderer.setSeriesOutlineStroke(0, new BasicStroke(1.5f)); // 数据点边框粗细
        renderer.setUseSeriesOffset(true); // 数据点偏移

        BufferedImage chartImage = chart.createBufferedImage(800, 600);

        File outputfile = new File("D://c/wordcloud2212.png");
        try {
            ImageIO.write(chartImage, "png", outputfile);
        } catch (IOException e) {
            e.printStackTrace();
        }

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            ChartUtils.writeBufferedImageAsPNG(outputStream, chartImage);
        } catch (IOException e) {
            e.printStackTrace();
        }
        byte[] chartBytes = outputStream.toByteArray();

        Records records = recordsMapper.selectById(id);
        records.setId(count);
        records.setTotal(total);
        records.setImage(chartBytes);
        return records;
    }

    public Records analysis2(Integer id, String param2) {
        int number = Integer.parseInt(param2);
        List<Records> records = recordsMapper.select2ById(id);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        // 创建一个映射，将日期作为键，对应的数据作为值
        Map<YearMonth, Double> monthlyMoneyMap = new LinkedHashMap<>();

        for (Records record : records) {
            LocalDateTime dateTime = LocalDateTime.parse(record.getTime(), formatter);
            YearMonth yearMonth = YearMonth.from(dateTime);
            Double money = record.getMoney().doubleValue();

            // 将数据添加到每月的金额中
            monthlyMoneyMap.merge(yearMonth, money, Double::sum);
        }

        System.out.println(monthlyMoneyMap);

        // 按日期排序并截取最近number个月的数据
        List<Map.Entry<YearMonth, Double>> entries = new ArrayList<>(monthlyMoneyMap.entrySet());
        entries.sort(Map.Entry.comparingByKey(Comparator.reverseOrder()));

        // 计算最近number个月的订单数
        Map<YearMonth, Double> truncatedMap = new LinkedHashMap<>();
        int totalOrders = 0;
        for (int i = 0; i < Math.min(number, entries.size()); i++) {
            Map.Entry<YearMonth, Double> entry = entries.get(i);
            truncatedMap.put(entry.getKey(), entry.getValue());

            // 统计订单数
            YearMonth yearMonth = entry.getKey();
            totalOrders += records.stream()
                    .filter(record -> YearMonth.from(LocalDateTime.parse(record.getTime(), formatter)).equals(yearMonth))
                    .count();
        }

        // 反转map，使日期按正序排列
        List<Map.Entry<YearMonth, Double>> myList = new ArrayList<>(truncatedMap.entrySet());
        Collections.reverse(myList);
        Map<YearMonth, Double> myMap = new LinkedHashMap<>();
        for (Map.Entry<YearMonth, Double> entry : myList) {
            myMap.put(entry.getKey(), entry.getValue());
        }

        double totalIncome = truncatedMap.values().stream()
                .mapToDouble(Double::doubleValue)
                .sum();

        // 创建数据集并添加数据
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        for (Map.Entry<YearMonth, Double> entry : myMap.entrySet()) {
            dataset.addValue(entry.getValue(), "Income", entry.getKey().toString());
        }

        // 创建折线图，设置属性等...
        JFreeChart chart = ChartFactory.createLineChart(
                "月收入变化趋势", // 图表标题
                "日期", // x轴标签
                "收入 (¥)", // y轴标签
                dataset, // 数据集
                PlotOrientation.VERTICAL,
                true, // Include Legend
                true, // Include Tooltips
                false // URLs not required
        );

        // 设置图表样式等...
        chart.setBackgroundPaint(new Color(255, 255, 200, 0)); // 背景透明
        chart.getTitle().setPaint(Color.DARK_GRAY); // 标题颜色
        chart.getTitle().setFont(new Font("SansSerif", Font.BOLD, 24));

        CategoryPlot plot = (CategoryPlot) chart.getPlot();
        plot.setBackgroundPaint(Color.WHITE); // 背景色
        plot.setDomainGridlinePaint(Color.GRAY); // 网格线颜色
        plot.setRangeGridlinePaint(Color.GRAY);

        CategoryAxis domainAxis = plot.getDomainAxis();
        domainAxis.setTickLabelPaint(Color.DARK_GRAY); // X轴标签颜色
        domainAxis.setTickLabelFont(new Font("SansSerif", Font.PLAIN, 20)); // X轴标签字体
        domainAxis.setLabelFont(new Font("SansSerif", Font.BOLD, 24)); // X轴标题字体

        NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
        rangeAxis.setTickLabelPaint(Color.blue); // Y轴标签颜色
        rangeAxis.setTickLabelFont(new Font("SansSerif", Font.PLAIN, 20)); // Y轴标签字体
        rangeAxis.setLabelFont(new Font("SansSerif", Font.BOLD, 24)); // Y轴标题字体

        LineAndShapeRenderer renderer = (LineAndShapeRenderer) plot.getRenderer();
        renderer.setSeriesPaint(0, new Color(0, 102, 255)); // 折线颜色
        renderer.setSeriesStroke(0, new BasicStroke(2.0f)); // 折线粗细
        renderer.setSeriesShapesVisible(0, true); // 显示数据点
        renderer.setSeriesShape(0, new Ellipse2D.Double(-3, -3, 6, 6)); // 数据点形状
        renderer.setSeriesOutlinePaint(0, Color.WHITE); // 数据点边框颜色
        renderer.setSeriesOutlineStroke(0, new BasicStroke(1.5f)); // 数据点边框粗细
        renderer.setUseSeriesOffset(true); // 数据点偏移

        BufferedImage chartImage = chart.createBufferedImage(800, 600);

        // 将图像转换为字节数组
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            ChartUtils.writeBufferedImageAsPNG(outputStream, chartImage);
        } catch (IOException e) {
            e.printStackTrace();
        }
        byte[] chartBytes = outputStream.toByteArray();

        Records result = recordsMapper.selectById(id);
        result.setImage(chartBytes);
        result.setId(totalOrders); // 设置最近6个月的总订单数
        result.setTotal(totalIncome); // 设置总收入
        result.setId(totalOrders);
        return result;
    }


    public Records analysis3(Integer id, String param2) {
        int number = Integer.parseInt(param2);
        List<Records> records = recordsMapper.select2ById(id);
        int count=records.size();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        // 创建一个映射，将日期作为键，对应的数据作为值
        Map<Year, Double> yearlyMoneyMap = new LinkedHashMap<>();
        for (Records record : records) {
            LocalDateTime dateTime = LocalDateTime.parse(record.getTime(), formatter);
            Year year = Year.from(dateTime);
            Double money = record.getMoney().doubleValue();

            // 将数据添加到每年的金额中
            yearlyMoneyMap.merge(year, money, Double::sum);
        }

        // 截取数据
        Year currentYear = null;
        int j = 0;
        Map<Year, Double> truncatedMap = new LinkedHashMap<>();
        for (Map.Entry<Year, Double> entry : yearlyMoneyMap.entrySet()) {
            truncatedMap.put(entry.getKey(), entry.getValue());
            j++;
            if (j == number) {
                break;
            }
        }
        int sum=0;

        double total = truncatedMap.values().stream()
                .mapToDouble(Double::doubleValue)
                .sum();
        // 创建数据集并添加数据
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        for (Map.Entry<Year, Double> entry : truncatedMap.entrySet()) {
            dataset.addValue(entry.getValue(), "Income", entry.getKey().toString());
        }

        // 创建折线图，设置属性等...
        JFreeChart chart = ChartFactory.createLineChart(
                "年收入变化趋势", // 图表标题
                "日期", // x轴标签
                "收入 (¥)", // y轴标签
                dataset, // 数据集
                PlotOrientation.VERTICAL,
                true, // Include Legend
                true, // Include Tooltips
                false // URLs not required
        );

        // 将图表封装到面板中
        // 设置图表样式等...
        chart.setBackgroundPaint(new Color(255, 255, 200, 0)); // 背景透明
        chart.getTitle().setPaint(Color.DARK_GRAY); // 标题颜色
        chart.getTitle().setFont(new Font("SansSerif", Font.BOLD, 24));

        CategoryPlot plot = (CategoryPlot) chart.getPlot();
        plot.setBackgroundPaint(Color.WHITE); // 背景色
        plot.setDomainGridlinePaint(Color.GRAY); // 网格线颜色
        plot.setRangeGridlinePaint(Color.GRAY);

        CategoryAxis domainAxis = plot.getDomainAxis();
        domainAxis.setTickLabelPaint(Color.DARK_GRAY); // X轴标签颜色
        domainAxis.setTickLabelFont(new Font("SansSerif", Font.PLAIN, 20)); // X轴标签字体
        domainAxis.setLabelFont(new Font("SansSerif", Font.BOLD, 24)); // X轴标题字体

        NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
        rangeAxis.setTickLabelPaint(Color.blue); // Y轴标签颜色
        rangeAxis.setTickLabelFont(new Font("SansSerif", Font.PLAIN, 20)); // Y轴标签字体
        rangeAxis.setLabelFont(new Font("SansSerif", Font.BOLD, 24)); // Y轴标题字体

        LineAndShapeRenderer renderer = (LineAndShapeRenderer) plot.getRenderer();
        renderer.setSeriesPaint(0, new Color(0, 102, 255)); // 折线颜色
        renderer.setSeriesStroke(0, new BasicStroke(2.0f)); // 折线粗细
        renderer.setSeriesShapesVisible(0, true); // 显示数据点
        renderer.setSeriesShape(0, new Ellipse2D.Double(-3, -3, 6, 6)); // 数据点形状
        renderer.setSeriesOutlinePaint(0, Color.WHITE); // 数据点边框颜色
        renderer.setSeriesOutlineStroke(0, new BasicStroke(1.5f)); // 数据点边框粗细
        renderer.setUseSeriesOffset(true); // 数据点偏移

        BufferedImage chartImage = chart.createBufferedImage(800, 600);

        // 将图像转换为字节数组
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            ChartUtils.writeBufferedImageAsPNG(outputStream, chartImage);
        } catch (IOException e) {
            e.printStackTrace();
        }
        byte[] chartBytes = outputStream.toByteArray();

        Records result = recordsMapper.selectById(id);
        result.setImage(chartBytes);
        result.setId(count);
        result.setTotal(total);
        return result;
    }

//    public Records income1(Integer id, String param2) {
//        int number = Integer.parseInt(param2);
//        List<Records> recordsList = recordsMapper.select2ById(id);
//
//        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
//
//        List<LocalDateTime> timeList = recordsList.stream()
//                .map(record -> LocalDateTime.parse(record.getTime(), formatter))
//                .collect(Collectors.toList());
//        List<BigDecimal> moneyList = recordsList.stream()
//                .map(Records::getMoney)
//                .collect(Collectors.toList());
//
//        Map<LocalDateTime, Double> timeMoneyMap = new HashMap<>();
//        for (int i = 0; i < timeList.size(); i++) {
//            timeMoneyMap.put(timeList.get(i), moneyList.get(i).doubleValue());
//        }
//
//        Map<LocalDateTime, Double> sortedMap = timeMoneyMap.entrySet().stream()
//                .sorted(Map.Entry.comparingByKey())
//                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
//                        (oldValue, newValue) -> oldValue, LinkedHashMap::new));
//
//        Map<LocalDate, Double> dailyMoneyMap = new LinkedHashMap<>();
//        LocalDate currentDate = null;
//        double dailyTotalMoney = 0;
//
//        for (Map.Entry<LocalDateTime, Double> entry : sortedMap.entrySet()) {
//            LocalDateTime dateTime = entry.getKey();
//            double money = entry.getValue();
//            LocalDate date = dateTime.toLocalDate();
//
//            if (currentDate == null || !currentDate.equals(date)) {
//                if (currentDate != null) {
//                    dailyMoneyMap.put(currentDate, dailyTotalMoney);
//                }
//                currentDate = date;
//                dailyTotalMoney = money;
//            } else {
//                dailyTotalMoney += money;
//            }
//        }
//
//        if (currentDate != null) {
//            dailyMoneyMap.put(currentDate, dailyTotalMoney);
//        }
//
//        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
//        for (Map.Entry<LocalDate, Double> entry : dailyMoneyMap.entrySet()) {
//            dataset.addValue(entry.getValue(), "Income", entry.getKey().toString());
//        }
//
//        JFreeChart chart = ChartFactory.createBarChart(
//                "日收入分布图",
//                "日期",
//                "收入 (¥)",
//                dataset,
//                PlotOrientation.VERTICAL,
//                true,
//                true,
//                false
//        );
//
//        styleChart(chart);
//
//        BufferedImage chartImage = chart.createBufferedImage(800, 600);
//
//        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
//        try {
//            ChartUtils.writeBufferedImageAsPNG(outputStream, chartImage);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        byte[] chartBytes = outputStream.toByteArray();
//
//        Records result = recordsMapper.selectById(id);
//        result.setImage(chartBytes);
//        result.setId(dailyMoneyMap.size());
//        result.setTotal(dailyMoneyMap.values().stream().mapToDouble(Double::doubleValue).sum());
//        return result;
//    }
//
//    private void styleChart(JFreeChart chart) {
//        chart.setBackgroundPaint(new Color(255, 255, 200, 0)); // 背景透明
//        chart.getTitle().setPaint(Color.DARK_GRAY); // 标题颜色
//        chart.getTitle().setFont(new Font("SansSerif", Font.BOLD, 24));
//
//        CategoryPlot plot = (CategoryPlot) chart.getPlot();
//        plot.setBackgroundPaint(Color.WHITE); // 背景色
//        plot.setDomainGridlinePaint(Color.GRAY); // 网格线颜色
//        plot.setRangeGridlinePaint(Color.GRAY);
//
//        CategoryAxis domainAxis = plot.getDomainAxis();
//        domainAxis.setTickLabelPaint(Color.DARK_GRAY); // X轴标签颜色
//        domainAxis.setTickLabelFont(new Font("SansSerif", Font.PLAIN, 20)); // X轴标签字体
//        domainAxis.setLabelFont(new Font("SansSerif", Font.BOLD, 50)); // X轴标题字体
//
//        NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
//        rangeAxis.setTickLabelPaint(Color.BLUE); // Y轴标签颜色
//        rangeAxis.setTickLabelFont(new Font("SansSerif", Font.PLAIN, 20)); // Y轴标签字体
//        rangeAxis.setLabelFont(new Font("SansSerif", Font.BOLD, 24)); // Y轴标题字体
//
//        BarRenderer renderer = (BarRenderer) plot.getRenderer();
//        renderer.setSeriesPaint(0, new Color(0, 102, 255)); // 柱状图颜色
//    }
    public Records income( Integer id) {
        List<Records> recordsList = recordsMapper.select2ById(id);

        // 获取收入数据
        List<BigDecimal> moneyList = recordsList.stream()
                .map(Records::getMoney)
                .collect(Collectors.toList());

        // 将BigDecimal转换为double数组
        double[] incomeData = moneyList.stream()
                .mapToDouble(BigDecimal::doubleValue)
                .toArray();

        // 创建收入直方图
        HistogramDataset dataset = new HistogramDataset();
        dataset.setType(HistogramType.FREQUENCY);
        dataset.addSeries("Income Distribution", incomeData, 10);

        String plotTitle = "总收入分布直方图";
        String xAxisLabel = "收入";
        String yAxisLabel = "频率";
        PlotOrientation orientation = PlotOrientation.VERTICAL;
        boolean show = true;
        boolean toolTips = true;
        boolean urls = false;

        JFreeChart histogram = ChartFactory.createHistogram(plotTitle, xAxisLabel, yAxisLabel,
                dataset, orientation, show, toolTips, urls);

        styleChart(histogram);

        BufferedImage histogramImage = histogram.createBufferedImage(800, 600);

        // 将图像转换为字节数组
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            ChartUtils.writeBufferedImageAsPNG(outputStream, histogramImage);
        } catch (IOException e) {
            e.printStackTrace();
        }
        byte[] histogramBytes = outputStream.toByteArray();

        Records result = recordsMapper.selectById(id);
        result.setImage(histogramBytes);
        return result;
    }

    private void styleChart(JFreeChart chart) {
        chart.setBackgroundPaint(new Color(255, 255, 200, 0)); // 背景透明
        chart.getTitle().setPaint(Color.DARK_GRAY); // 标题颜色
        chart.getTitle().setFont(new Font("SansSerif", Font.BOLD, 24));

        XYPlot plot = (XYPlot) chart.getPlot();
        plot.setBackgroundPaint(Color.WHITE); // 背景色
        plot.setDomainGridlinePaint(Color.GRAY); // 网格线颜色
        plot.setRangeGridlinePaint(Color.GRAY);

        ValueAxis domainAxis = plot.getDomainAxis();
        domainAxis.setTickLabelPaint(Color.DARK_GRAY); // X轴标签颜色
        domainAxis.setTickLabelFont(new Font("SansSerif", Font.PLAIN, 20)); // X轴标签字体
        domainAxis.setLabelFont(new Font("SansSerif", Font.BOLD, 24)); // X轴标题字体

        NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
        rangeAxis.setTickLabelPaint(Color.BLUE); // Y轴标签颜色
        rangeAxis.setTickLabelFont(new Font("SansSerif", Font.PLAIN, 20)); // Y轴标签字体
        rangeAxis.setLabelFont(new Font("SansSerif", Font.BOLD, 24)); // Y轴标题字体

        XYBarRenderer renderer = (XYBarRenderer) plot.getRenderer();
        renderer.setSeriesPaint(0, new Color(0, 102, 255)); // 柱状图颜色
    }








}