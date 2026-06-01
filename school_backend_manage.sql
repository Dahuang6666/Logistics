-- MySQL dump 10.13  Distrib 8.0.36, for Win64 (x86_64)
--
-- Host: localhost    Database: school_backend_manage
-- ------------------------------------------------------
-- Server version	8.0.36

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Current Database: `school_backend_manage`
--

CREATE DATABASE /*!32312 IF NOT EXISTS*/ `school_backend_manage` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;

USE `school_backend_manage`;

--
-- Table structure for table `announcement`
--

DROP TABLE IF EXISTS `announcement`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `announcement` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '公告ID',
  `title` varchar(100) NOT NULL COMMENT '公告标题',
  `content` text NOT NULL COMMENT '公告内容',
  `publisher` varchar(50) NOT NULL COMMENT '发布单位/人',
  `publish_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '发布时间',
  `is_deleted` tinyint(1) DEFAULT '0' COMMENT '是否删除（0-正常，1-已删除）',
  `announcement_type_id` int NOT NULL COMMENT '公告类型id',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=30 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='公告表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `announcement`
--

LOCK TABLES `announcement` WRITE;
/*!40000 ALTER TABLE `announcement` DISABLE KEYS */;
INSERT INTO `announcement` VALUES (6,'【紧急】关于2025年春季学期开学时间调整的通知','各位同学：因疫情防控需要，原定2月20日的开学时间调整为3月1日，请同学们合理安排返校行程。具体安排另行通知。\n22','教务处','2026-05-10 16:19:14',0,2),(7,'紧急停电通知','因设备故障抢修，1号教学楼将于今日15:00-18:00紧急停电，请师生做好应急准备。','后勤处','2025-01-12 13:30:00',0,2),(8,'寒假期间校园安全提醒','寒假即将来临，请各位同学注意人身安全、财产安全、网络安全。离校前请关好门窗、切断电源，贵重物品随身携带。','保卫处','2024-12-28 10:00:00',0,3),(10,'关于调整学生请假制度的通知','自2025年3月1日起，学生请假流程将启用新的线上审批系统。请假3天以内由辅导员审批，超过3天需学院领导审批。','学生处','2026-03-03 15:33:58',0,4),(11,'图书借阅制度更新通知','为提高图书流通效率，自2月1日起，图书借阅期限由30天调整为45天，续借次数由1次增加到2次。','图书馆','2025-01-05 09:30:00',0,4),(13,'英语四六级考试成绩查询通知','2024年下半年全国大学英语四、六级考试成绩将于2月25日上午10时发布，考生可登录中国教育考试网查询成绩。','教务处','2024-12-25 16:00:00',0,5),(14,'食堂就餐时间调整通知','为方便师生用餐，自下周起食堂营业时间调整为：早餐6:30-9:00，午餐11:00-13:30，晚餐17:00-19:30。','后勤处','2024-12-18 10:30:00',0,5),(15,'2025年春季运动会报名开始','我校将于3月15日举办春季田径运动会，现开始接受报名。报名截止时间：2月28日。请到体育部或线上系统报名。','体育部','2025-01-06 14:00:00',0,6),(16,'创新创业大赛启动仪式','我校第八届\"互联网+\"大学生创新创业大赛将于3月1日举行启动仪式，诚邀各位同学积极参与，展现创新才华！','团委','2024-12-22 11:30:00',0,6),(17,'新年音乐会演出通知','校艺术团将于2月10日晚19:00在大礼堂举办新年音乐会，免费向全校师生开放，座位有限，先到先得！','艺术团','2024-12-15 13:00:00',0,6),(18,'图书馆延长开放时间温馨提示','为方便同学们期末复习，图书馆自本周起延长开放时间至晚上23:00，请同学们注意返回宿舍安全。','图书馆','2025-01-11 17:00:00',0,7),(19,'天气降温温馨提示','根据气象部门预报，未来三天气温将大幅下降，请同学们注意添加衣物，预防感冒。','校医院','2026-03-03 15:44:11',0,7),(20,'校园快递收取提示','春节前快递量较大，请同学们及时查收快递，避免滞留。快递站营业时间：9:00-21:00。','后勤处','2024-12-28 12:00:00',0,7),(21,'关于加强冬季宿舍安全管理的紧急通知','各位同学：\r\n     近期气温骤降，为确保全体学生的生命财产安全，学校决定采取以下冬季安全管理措施：\r\n\r\n     一、用电安全：\r\n     1. 严禁在宿舍内使用大功率电器，包括但不限于电热毯、电暖器、电磁炉、热得快等。\r\n     2. 不得私拉乱接电线，不得超负荷用电。\r\n     3. 离开宿舍时务必切断所有电源，拔掉插头。\r\n     4. 手机充电完成后及时拔掉充电器，防止长时间充电引发火灾。\r\n\r\n     二、消防安全：\r\n     1. 宿舍内严禁使用明火，包括点蜡烛、燃烧纸张等行为。\r\n     2. 不得在宿舍内吸烟，违者将受到严肃处理。\r\n     3. 保持消防通道畅通，不得在走廊堆放杂物。\r\n     4. 熟悉宿舍楼内消防设施位置，掌握灭火器使用方法。\r\n\r\n     三、防盗安全：\r\n     1. 离开宿舍时务必锁好门窗，贵重物品妥善保管。\r\n     2. 不要将宿舍钥匙借给他人使用。\r\n     3. 发现可疑人员及时向宿管员或保卫处报告。\r\n\r\n     学校将组织安全检查小组不定期进行宿舍安全检查，对违反规定的行为将依据校规校纪严肃处理。希望各位同学高度重视，共同营造安全和谐的宿舍环境。\r\n\r\n     如有疑问，请联系学生处：电话0551-12345678\r\n\r\n     学生工作处\r\n     2024年12月30日','学生工作处','2024-12-30 09:00:00',0,2),(22,'期末考试期间图书馆服务调整及温馨提示','亲爱的同学们：\r\n\r\n     期末考试即将来临，为了给大家提供更好的学习环境，图书馆将在考试期间调整服务时间和相关安排，具体如下：\r\n\r\n     【开放时间调整】\r\n     自2025年1月5日起至1月20日考试结束：\r\n     - 周一至周日：早上7:00 - 晚上23:30（延长1.5小时）\r\n     - 自习室24小时开放（需刷卡进入）\r\n\r\n     【座位预约】\r\n     考虑到期末复习需求激增，图书馆实行座位预约制度：\r\n     1. 通过\"智慧校园\"APP提前预约座位\r\n     2. 每人每天最多预约一个时间段（上午/下午/晚上）\r\n     3. 预约后15分钟内未到视为自动放弃\r\n     4. 离开超过30分钟座位将被释放\r\n\r\n     【学习资源】\r\n     1. 开放多媒体学习室，提供电脑和投影设备\r\n     2. 增设打印、复印自助服务点\r\n     3. 专业课参考书籍集中展示区（3楼西侧）\r\n     4. 提供期末复习资料借阅专区\r\n\r\n     【温馨提示】\r\n     1. 请保持安静，不要大声喧哗或接打电话\r\n     2. 禁止占座，请合理利用公共资源\r\n     3. 爱护图书和设施，损坏需照价赔偿\r\n     4. 注意个人物品安全，贵重物品随身携带\r\n     5. 保持环境卫生，垃圾请丢入指定垃圾桶\r\n\r\n     【咨询服务】\r\n     如有任何问题，可通过以下方式联系我们：\r\n     - 服务台电话：0551-87654321\r\n     - 微信公众号：\"XX大学图书馆\"\r\n     - 咨询邮箱：library@xxu.edu.cn\r\n\r\n     祝各位同学期末考试顺利，取得优异成绩！\r\n\r\n     XX大学图书馆\r\n     2024年12月28日','图书馆','2024-12-28 10:30:00',0,7),(23,'第六届\"温馨宿舍·和谐家园\"宿舍文化节活动方案','各位同学：\r\n\r\n     为丰富校园文化生活，营造温馨和谐的宿舍氛围，增强同学们的集体荣誉感和归属感，学校决定举办第六届\"温馨宿舍·和谐家园\"宿舍文化节活动。现将有关事项通知如下：\r\n\r\n     【活动主题】\r\n     温馨宿舍·和谐家园 —— 共建美好寝室生活\r\n\r\n     【活动时间】\r\n     2025年1月10日 - 1月25日\r\n\r\n     【活动内容】\r\n\r\n     一、\"最美宿舍\"评比大赛\r\n     1. 参赛对象：全校所有宿舍\r\n     2. 评比标准：\r\n        - 卫生状况（30分）：整洁度、物品摆放、地面清洁\r\n        - 文化氛围（25分）：装饰布置、文化墙、个性特色\r\n        - 安全规范（25分）：用电安全、消防意识、违禁品检查\r\n        - 人文关怀（20分）：和谐相处、互助友爱、集体活动\r\n     3. 奖项设置：\r\n        - 一等奖10个（奖金500元+荣誉证书）\r\n        - 二等奖20个（奖金300元+荣誉证书）\r\n        - 三等奖30个（奖金200元+荣誉证书）\r\n        - 优秀奖若干（荣誉证书）\r\n\r\n     二、\"寝室才艺秀\"展演活动\r\n     1. 形式不限：歌舞、小品、相声、魔术、乐器演奏等\r\n     2. 每个宿舍限报一个节目，时长5-8分钟\r\n     3. 初赛时间：1月12日晚19:00，地点：大礼堂\r\n     4. 决赛时间：1月20日晚19:00，地点：大礼堂\r\n\r\n     三、\"宿舍摄影大赛\"\r\n     1. 主题：记录宿舍生活的美好瞬间\r\n     2. 作品要求：原创摄影作品，宿舍相关主题\r\n     3. 提交方式：发送至邮箱dormitory@xxu.edu.cn\r\n     4. 获奖作品将在校园展出并颁发证书及奖品\r\n\r\n     四、\"舍长座谈会\"\r\n     邀请各宿舍舍长代表参加座谈会，就宿舍管理、服务提升等问题进行交流讨论。\r\n     时间：1月15日下午15:00\r\n     地点：学生活动中心三楼会议室\r\n\r\n     【报名方式】\r\n     1. 扫描海报二维码在线报名\r\n     2. 到各楼栋宿管办公室现场报名\r\n     3. 报名截止时间：2025年1月8日17:00\r\n\r\n     【注意事项】\r\n     1. 所有参赛宿舍需遵守学校宿舍管理规定\r\n     2. 评比过程中将进行随机抽查\r\n     3. 发现弄虚作假行为，取消参赛资格\r\n     4. 活动最终解释权归学生工作处所有\r\n\r\n     让我们一起行动起来，把宿舍打造成温馨的家园！\r\n\r\n     联系人：张老师\r\n     联系电话：0551-12345678\r\n\r\n     学生工作处\r\n     2025年1月3日','学生工作处','2026-03-03 15:38:47',0,6),(24,'关于防范电信网络诈骗的安全提醒','各位同学：\r\n\r\n     近期，我校及周边高校发生多起针对学生的电信网络诈骗案件，给受害学生造成了严重的经济损失。为提高大家的防范意识，保护人身财产安全，现就常见诈骗手段及防范措施提醒如下：\r\n\r\n     【常见诈骗类型】\r\n\r\n     一、冒充公检法诈骗\r\n     骗子冒充公安、检察院、法院工作人员，谎称受害人涉嫌洗钱、贩毒等犯罪，要求转账到\"安全账户\"配合调查。\r\n\r\n     二、网络兼职刷单诈骗\r\n     以高额佣金为诱饵，诱导学生进行刷单，前几单会返还本金和佣金，后续以各种理由要求继续刷单并拒不返款。\r\n\r\n     三、冒充熟人诈骗\r\n     通过QQ、微信等社交软件，盗取或仿冒熟人账号，以各种理由向好友借钱。\r\n\r\n     四、网购退款诈骗\r\n     自称电商平台或快递客服，谎称商品有质量问题需要退款，诱导添加好友后要求扫码或点击链接进行退款操作。\r\n\r\n     五、校园贷、套路贷诈骗\r\n     以无抵押、低利息为幌子，诱导学生贷款，实际签订高额借款合同，陷入还款陷阱。\r\n\r\n     六、冒充老师或辅导员诈骗\r\n     通过QQ群、微信群等渠道，冒充老师或辅导员，以收取培训费、资料费等名义要求转账。\r\n\r\n     【防范措施】\r\n\r\n     1. 提高警惕，保护个人信息\r\n     不要轻易向陌生人透露个人身份信息、银行卡号、验证码等重要信息。\r\n\r\n     2. 核实身份，谨慎转账\r\n     接到要求转账的电话或信息时，一定要通过其他渠道核实对方身份，不要轻易转账。\r\n\r\n     3. 不贪小便宜，警惕高额回报\r\n     天上不会掉馅饼，凡是号称\"高回报、低风险\"的投资理财都要提高警惕。\r\n\r\n     4. 正规渠道，拒绝校园贷\r\n     如有资金需求，通过正规金融机构办理，不要被非法校园贷所诱惑。\r\n\r\n     5. 安装安全软件\r\n     在手机和电脑上安装正规的安全防护软件，及时更新系统补丁。\r\n\r\n     6. 遇到诈骗及时报警\r\n     一旦发现被骗，立即拨打110报警，并保存好相关证据。\r\n\r\n     【紧急联系方式】\r\n     - 校园110：0551-110110\r\n     - 保卫处值班电话：0551-87654321\r\n     - 辖区派出所：0551-96110\r\n\r\n     请各位同学务必提高警惕，增强防范意识，保护好自己的财产安全。同时也请大家互相提醒，共同营造安全的校园环境。\r\n\r\n     保卫处\r\n     2024年12月25日','保卫处','2024-12-25 16:00:00',0,3),(25,'学生宿舍管理规定（2025年修订版）','各位同学：\r\n\r\n     为维护正常的宿舍秩序，保障学生的学习和生活，根据教育部《普通高等学校学生管理规定》及学校实际情况，特制定本规定。\r\n\r\n     第一章 总则\r\n\r\n     第一条 学生宿舍是学生在校期间学习、生活的重要场所，是开展思想政治教育和素质教育的重要阵地。\r\n     第二条 学生应当遵守国家法律法规和学校各项规章制度，自觉维护宿舍正常秩序。\r\n\r\n     第二章 住宿管理\r\n\r\n     第三条 学生入住宿舍实行统一分配，不得擅自调换或转让床位。\r\n     第四条 学生应按时归宿，严格遵守作息时间：\r\n     - 周日至周四：23:00熄灯就寝\r\n     - 周五至周六：23:30熄灯就寝\r\n     - 早上6:30开门\r\n     第五条 学生不得留宿他人，异性不得进入学生宿舍。\r\n     第六条 学生外出必须向宿管员请假登记，按时返校。\r\n\r\n     第三章 安全管理\r\n\r\n     第七条 严禁在宿舍内存放、使用违禁物品：\r\n     （一）易燃易爆物品、有毒有害物品\r\n     （二）管制刀具及其他危险品\r\n     （三）大功率电器（超过800W）\r\n     （四）酒精炉、煤油炉等明火器具\r\n     （五）违章电器：电热毯、热得快、电磁炉等\r\n\r\n     第八条 严禁私拉乱接电线，严禁超负荷用电。\r\n     第九条 发现安全隐患应及时报告，不得隐瞒。\r\n\r\n     第四章 卫生管理\r\n\r\n     第十条 宿舍实行值日制度，保持室内整洁：\r\n     （一）每天清扫地面，保持地面干净\r\n     （二）定期清理床铺，被褥叠放整齐\r\n     （三）物品摆放有序，不乱扔垃圾\r\n     （四）定期开窗通风，保持空气清新\r\n\r\n     第十一条 学校每周进行一次卫生检查，检查结果纳入综合测评。\r\n\r\n     第五章 文明公约\r\n\r\n     第十二条 宿舍成员应当互相尊重，和睦相处，不得有以下行为：\r\n     （一）大声喧哗，影响他人休息\r\n     （二）播放音乐、视频声音过大\r\n     （三）在宿舍内吸烟、酗酒\r\n     （四）赌博或变相赌博\r\n     （五）传播不良信息\r\n\r\n     第十三条 爱护公共设施，损坏照价赔偿。\r\n\r\n     第六章 奖惩办法\r\n\r\n     第十四条 学校设立\"文明宿舍\"评选制度，对表现优秀的宿舍给予表彰奖励。\r\n     第十五条 违反本规定的，视情节轻重给予批评教育、通报批评、纪律处分等处理。\r\n\r\n     第七章 附则\r\n\r\n     第十六条 本规定自2025年1月1日起施行，由学生工作处负责解释。\r\n     第十七条 各学院可根据本规定制定实施细则。\r\n\r\n     学生工作处\r\n     2024年12月20日','学生工作处','2024-12-20 11:00:00',0,4),(29,'Test001','eee','admin','2026-05-10 16:19:49',1,2);
/*!40000 ALTER TABLE `announcement` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `announcement_type`
--

DROP TABLE IF EXISTS `announcement_type`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `announcement_type` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '类型ID',
  `type_name` varchar(50) NOT NULL COMMENT '类型名称',
  `priority` int DEFAULT '0' COMMENT '优先级',
  `created_date` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='公告类型表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `announcement_type`
--

LOCK TABLES `announcement_type` WRITE;
/*!40000 ALTER TABLE `announcement_type` DISABLE KEYS */;
INSERT INTO `announcement_type` VALUES (1,'未知类型',1,'2026-03-03 14:59:48'),(2,'紧急通知',3,'2025-12-29 15:00:00'),(3,'安全提醒',3,'2025-12-29 15:00:00'),(4,'制度通知',2,'2025-12-29 15:00:00'),(5,'日常通知',1,'2025-12-29 15:00:00'),(6,'活动公告',1,'2025-12-29 15:00:00'),(7,'温馨提示',1,'2025-12-29 15:00:00');
/*!40000 ALTER TABLE `announcement_type` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `build`
--

DROP TABLE IF EXISTS `build`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `build` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `building_number` varchar(10) NOT NULL COMMENT '楼号',
  `assigned_gender` enum('男','女') NOT NULL COMMENT '该楼对应的性别：男或女',
  `description` varchar(255) DEFAULT NULL COMMENT '备注信息',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `status` enum('正常','异常') DEFAULT '正常',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='宿舍楼表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `build`
--

LOCK TABLES `build` WRITE;
/*!40000 ALTER TABLE `build` DISABLE KEYS */;
INSERT INTO `build` VALUES (1,'10','男','一个普通个宿舍','2025-07-06 22:35:42','2025-07-06 22:35:44','正常'),(2,'9','女',NULL,'2026-01-04 16:00:32','2026-01-04 16:00:32','正常'),(3,'8','男',NULL,'2026-01-04 16:01:05','2026-01-04 16:01:05','正常'),(5,'7','女','普通的宿舍楼\n','2026-03-01 16:31:17','2026-03-01 16:31:17','正常'),(6,'5','男','qqq','2026-05-10 16:21:09','2026-05-10 16:21:09','正常');
/*!40000 ALTER TABLE `build` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `complaint_suggestion`
--

DROP TABLE IF EXISTS `complaint_suggestion`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `complaint_suggestion` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '投诉建议ID',
  `user_no` varchar(20) NOT NULL COMMENT '提交人学号',
  `content` text NOT NULL COMMENT '投诉/建议内容',
  `status` enum('未处理','已处理') DEFAULT '未处理' COMMENT '处理状态',
  `reply` text COMMENT '处理回复',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '提交时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint(1) DEFAULT '0' COMMENT '是否删除（0-正常，1-删除）',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='投诉与建议表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `complaint_suggestion`
--

LOCK TABLES `complaint_suggestion` WRITE;
/*!40000 ALTER TABLE `complaint_suggestion` DISABLE KEYS */;
INSERT INTO `complaint_suggestion` VALUES (1,'2023001','test','已处理','ok','2025-07-22 23:29:34','2026-02-27 09:35:45',1),(2,'202512','test','已处理','aaa','2026-01-03 12:08:10','2026-02-27 10:04:18',1),(3,'202512','test2','已处理','ok,这是一个测试回复','2026-01-03 12:09:06','2026-01-21 14:51:54',0),(5,'202512','晚上为什么断电😡','已处理','学校规定同学','2026-01-21 14:56:38','2026-01-21 14:58:02',0),(6,'202601','Test','未处理',NULL,'2026-01-27 16:16:31','2026-01-27 16:16:31',0),(7,'20260510','Test','未处理',NULL,'2026-05-10 15:28:53','2026-05-10 15:28:53',0),(8,'2026001','qqq','未处理',NULL,'2026-05-10 16:05:07','2026-05-10 16:05:07',0),(9,'2026003','qqqq','已处理','ok','2026-05-10 16:16:23','2026-05-10 16:23:57',0);
/*!40000 ALTER TABLE `complaint_suggestion` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `dorm_change_application`
--

DROP TABLE IF EXISTS `dorm_change_application`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `dorm_change_application` (
  `application_id` int NOT NULL AUTO_INCREMENT,
  `student_no` varchar(20) NOT NULL COMMENT '学号',
  `current_dormitory_id` int NOT NULL COMMENT '当前宿舍id',
  `target_dormitory_id` int NOT NULL COMMENT '分配宿舍id',
  `reason` text COMMENT '调换原因',
  `application_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '申请时间',
  `status` enum('PENDING','APPROVED','REJECTED') NOT NULL DEFAULT 'PENDING' COMMENT 'PENDING，待审批；APPROVED，已同意；REJECTED，已拒绝',
  `approval_time` datetime DEFAULT NULL COMMENT '审批时间',
  `approver_no` varchar(20) DEFAULT NULL COMMENT '审批人工号',
  `comment` text COMMENT '意见',
  `is_deleted` tinyint DEFAULT '0' COMMENT '是否删除（0=正常，1=删除）',
  PRIMARY KEY (`application_id`)
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='调换宿舍申请表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `dorm_change_application`
--

LOCK TABLES `dorm_change_application` WRITE;
/*!40000 ALTER TABLE `dorm_change_application` DISABLE KEYS */;
INSERT INTO `dorm_change_application` VALUES (2,'2003001',1,2,'String','2025-07-10 00:19:10','PENDING','2025-07-10 21:15:13','ADMIN001','String',0),(3,'2003002',1,2,'String','2025-07-10 00:20:42','APPROVED','2026-05-05 23:09:38','ADMIN001','1',0),(6,'202512',2,6,'test','2026-01-27 14:46:23','PENDING','2026-05-05 22:38:31','ADMIN001','bu ok',1),(7,'202512',2,1,'test2','2026-01-27 15:37:39','APPROVED','2026-05-05 23:01:17','ADMIN001','1',0),(8,'202512',2,6,'Test3','2026-01-27 15:40:42','APPROVED','2026-05-10 15:42:42','ADMIN001','',0),(9,'202601',1,0,'Test','2026-01-27 16:16:16','APPROVED','2026-05-05 22:38:13','ADMIN001','202601',0),(10,'20260510',1,6,'test','2026-05-10 15:28:35','APPROVED','2026-05-10 15:32:24','ADMIN001','ok',0),(11,'2026001',1,0,'test','2026-05-10 16:04:33','PENDING',NULL,NULL,NULL,0),(12,'2026003',1,7,'test','2026-05-10 16:15:55','APPROVED','2026-05-10 16:23:33','ADMIN001','ok',0);
/*!40000 ALTER TABLE `dorm_change_application` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `dormitory`
--

DROP TABLE IF EXISTS `dormitory`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `dormitory` (
  `dormitory_id` int NOT NULL AUTO_INCREMENT,
  `dormitory_no` varchar(20) NOT NULL COMMENT '宿舍号',
  `building_id` tinyint NOT NULL COMMENT '宿舍楼',
  `capacity` int NOT NULL COMMENT '容量',
  `available_beds` int NOT NULL COMMENT '剩余床位',
  `status` enum('正常','异常') DEFAULT '正常' COMMENT '状态',
  `is_delete` tinyint DEFAULT '0' COMMENT '0 正常 1 删除',
  PRIMARY KEY (`dormitory_id`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='宿舍表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `dormitory`
--

LOCK TABLES `dormitory` WRITE;
/*!40000 ALTER TABLE `dormitory` DISABLE KEYS */;
INSERT INTO `dormitory` VALUES (1,'323',1,8,5,'正常',0),(2,'332',1,8,8,'正常',0),(3,'132',2,8,8,'正常',0),(4,'203',1,8,8,'正常',0),(5,'322',2,8,8,'正常',0),(6,'301',1,8,6,'正常',0),(7,'501',6,8,5,'正常',0);
/*!40000 ALTER TABLE `dormitory` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `repair_application`
--

DROP TABLE IF EXISTS `repair_application`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `repair_application` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '报修单ID',
  `user_no` varchar(20) NOT NULL COMMENT '申请人学号',
  `dormitory_id` int NOT NULL COMMENT '宿舍ID',
  `content` text NOT NULL COMMENT '报修内容',
  `image_url` varchar(255) DEFAULT NULL COMMENT '报修图片路径',
  `status` enum('待处理','处理中','已完成') DEFAULT '待处理' COMMENT '工单状态',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '申请时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后更新时间',
  `is_deleted` tinyint(1) DEFAULT '0' COMMENT '是否删除（0-正常 1-已删除）',
  `repair_type` varchar(50) DEFAULT '' COMMENT 'AI识别的报修类型',
  `urgency_level` enum('高','中','低') DEFAULT '中' COMMENT '紧急程度',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=29 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='维修申请表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `repair_application`
--

LOCK TABLES `repair_application` WRITE;
/*!40000 ALTER TABLE `repair_application` DISABLE KEYS */;
INSERT INTO `repair_application` VALUES (1,'2023001',1,'床坏了','','处理中','2025-07-20 22:34:37','2026-03-01 17:12:44',0,'家具损坏','中'),(13,'202512',1,'我的床有个洞','','待处理','2026-01-01 22:55:33','2026-01-24 17:48:31',1,'家具损坏','低'),(14,'202512',1,'窗户碎了，在漏风','','待处理','2026-01-01 22:57:18','2026-01-21 09:42:24',0,'门窗损坏','中'),(15,'202512',1,'窗户碎了，在漏风,我们很冷','','已完成','2026-01-01 22:59:39','2026-03-01 17:07:13',0,'门窗损坏','高'),(16,'202512',2,'灯泡不亮了','http://localhost:8080/imgs/repair_ccdd78f3-93c5-470c-9d61-d36cc2e87969.png','已完成','2026-01-21 10:59:03','2026-01-24 17:23:09',0,'电路故障','低'),(17,'202512',2,'test333','','待处理','2026-01-24 17:23:52','2026-01-24 17:24:04',1,'其他','低'),(18,'202512',2,'1','','待处理','2026-01-24 17:41:25','2026-01-24 17:41:49',1,'水管损坏','高'),(19,'202512',2,'2','','待处理','2026-01-24 17:41:32','2026-01-24 17:41:53',1,'水管损坏','高'),(20,'202601',1,'Test','http://localhost:8080/imgs/repair_ffe10546-8e15-4b37-a9f9-fa612dcf608d.jpg','已完成','2026-01-27 16:15:26','2026-03-01 17:06:29',0,'其他','低'),(21,'202601',1,'我问问','','待处理','2026-02-25 15:20:56','2026-02-25 15:25:07',1,'其他','低'),(22,'202601',1,'漏水了，地板淹了','http://localhost:8080/imgs/repair_2237f971-eabd-4ef1-9074-35df4507e5c9.jpg','待处理','2026-04-25 19:31:47','2026-05-15 00:20:05',1,'水管损坏','中'),(23,'202601',1,'漏水了，地板淹了','http://localhost:8080/imgs/repair_71e77a1d-b657-42b7-8226-c94118242f6e.jpg','待处理','2026-04-25 19:33:33','2026-04-25 19:44:21',1,'水管损坏','高'),(24,'202601',1,'漏水了，地板淹了','http://localhost:8080/imgs/repair_feb2fdfb-032c-422a-a92c-5d793c49c54c.png','待处理','2026-04-25 19:44:03','2026-04-25 19:44:03',0,'水管损坏','高'),(25,'202601',1,'漏水','','待处理','2026-05-08 08:36:56','2026-05-08 08:38:05',1,'水管损坏','高'),(26,'20260510',1,'灯泡不亮','http://localhost:8080/imgs/repair_ae8b48b2-6164-4a98-97a8-58241d4489b2.png','已完成','2026-05-10 15:26:46','2026-05-10 15:32:08',0,'电路故障','低'),(27,'2026001',1,'灯泡不亮','http://localhost:8080/imgs/repair_c27c24a6-fb10-4475-87b1-71a6301dc7df.png','待处理','2026-05-10 16:03:46','2026-05-10 16:03:46',0,'电路故障','低'),(28,'2026003',1,'灯不亮了','http://localhost:8080/imgs/repair_41be1d9e-0ded-4607-b5ac-d91abc266fbd.jpg','已完成','2026-05-10 16:15:03','2026-05-10 16:22:56',1,'电路故障','中');
/*!40000 ALTER TABLE `repair_application` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `role`
--

DROP TABLE IF EXISTS `role`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `role` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '角色ID',
  `role_name` varchar(50) NOT NULL COMMENT '角色名称',
  `role_code` varchar(50) NOT NULL COMMENT '角色编码',
  `description` varchar(255) DEFAULT NULL COMMENT '角色描述',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='角色表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `role`
--

LOCK TABLES `role` WRITE;
/*!40000 ALTER TABLE `role` DISABLE KEYS */;
INSERT INTO `role` VALUES (1,'学生','ROLE_STUDENT','普通学生用户，可提交报修、申请换宿舍'),(4,'系统管理员','ROLE_ADMIN','系统超级管理员，拥有所有权限');
/*!40000 ALTER TABLE `role` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `student_dormitory_info`
--

DROP TABLE IF EXISTS `student_dormitory_info`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `student_dormitory_info` (
  `id` int NOT NULL AUTO_INCREMENT,
  `user_no` varchar(20) NOT NULL,
  `building_id` tinyint DEFAULT NULL COMMENT '住宿楼编号id',
  `dormitory_number` varchar(10) DEFAULT NULL COMMENT '宿舍号',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_info_completed` tinyint DEFAULT '0' COMMENT '是否完成首次信息填写(0:未完成,1:已完成)',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=30 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='学生住宿表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `student_dormitory_info`
--

LOCK TABLES `student_dormitory_info` WRITE;
/*!40000 ALTER TABLE `student_dormitory_info` DISABLE KEYS */;
INSERT INTO `student_dormitory_info` VALUES (1,'2023001',1,'323','2025-07-06 13:28:41','2025-07-09 22:30:50',1),(2,'2023002',1,'322','2025-07-06 13:29:36','2025-07-09 22:30:50',1),(3,'2023003',1,'203','2025-07-06 13:30:03','2025-07-09 22:30:50',1),(18,'202512',1,'301','2025-12-22 17:26:19','2026-05-10 15:42:41',1),(22,'202601',1,'323','2026-01-04 16:13:06','2026-01-04 16:13:34',1),(23,'123456',2,'132','2026-01-04 16:16:14','2026-01-04 16:22:53',1),(24,'2003002',1,'332','2026-05-05 23:09:37','2026-05-05 23:09:37',1),(25,'20260510',1,'301','2026-05-10 15:04:12','2026-05-10 15:32:23',1),(26,'2026001',1,'323','2026-05-10 16:01:08','2026-05-10 16:01:56',1),(27,'2026003',6,'501','2026-05-10 16:12:30','2026-05-10 16:23:32',1),(28,'20260515',6,'501','2026-05-15 09:12:08','2026-05-15 09:13:07',1),(29,'2026006',6,'501','2026-05-15 10:22:43','2026-05-15 10:23:59',1);
/*!40000 ALTER TABLE `student_dormitory_info` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user`
--

DROP TABLE IF EXISTS `user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `user_no` varchar(20) NOT NULL COMMENT '学号/工号',
  `password` varchar(100) NOT NULL COMMENT '加密密码',
  `username` varchar(50) DEFAULT NULL COMMENT '姓名',
  `role` tinyint NOT NULL DEFAULT '1' COMMENT '角色: 1-学生, 4-系统管理员',
  `phone` varchar(20) DEFAULT NULL COMMENT '手机号',
  `email` varchar(50) DEFAULT NULL COMMENT '邮箱',
  `status` tinyint NOT NULL DEFAULT '1' COMMENT '状态: 1-正常, 0-禁用',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `gender` enum('男','女') DEFAULT NULL,
  `avatar_url` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_user_no` (`user_no`)
) ENGINE=InnoDB AUTO_INCREMENT=38 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='用户表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user`
--

LOCK TABLES `user` WRITE;
/*!40000 ALTER TABLE `user` DISABLE KEYS */;
INSERT INTO `user` VALUES (1,'2023001','$2a$10$iWscnO1ehbu13AxdAoR/ferB0u73a1SWk8ROcUDHb4CrWaCSM7jP.','张三',1,'13800000002','zhangsan@example.com',1,'2025-07-05 16:35:32','男',NULL),(2,'2023002','$2a$10$iWscnO1ehbu13AxdAoR/ferB0u73a1SWk8ROcUDHb4CrWaCSM7jP.','李四',1,'13800000002','lisi@example.com',1,'2025-07-05 16:35:32','男',NULL),(3,'2023003','$2a$10$iWscnO1ehbu13AxdAoR/ferB0u73a1SWk8ROcUDHb4CrWaCSM7jP.','王五',1,'13800000003','wangwu@example.com',1,'2025-07-05 16:35:32','女',NULL),(8,'ADMIN001','$2a$10$iWscnO1ehbu13AxdAoR/ferB0u73a1SWk8ROcUDHb4CrWaCSM7jP.','管理员',4,'13800000030','admin@example.com',1,'2025-07-05 16:35:32','男',NULL),(25,'202512','$2a$10$tgf4KsE/yliQn59S09B0pOjF.5nu1vSAy235oxV8CE9Vdbe9eBiUi','大黄',1,'18894456743','3559232235@qq.com',1,'2025-12-22 17:26:20','男','http://localhost:8080/imgs/user_0b9c77c4-c759-4e81-b37f-8b1acb13e8f9.jpg'),(27,'202601','$2a$10$dEc5vpp8he2Mcce7JAsMAOj2v4UVEL4nJAZ7Y7TKUsuyAJRMa3Jei','朝闻道',1,'18894456743','1514055091@qq.com',1,'2026-01-04 16:13:07','男','http://localhost:8080/imgs/user_2b6115c2-e7f3-481d-8c92-a6961921430a.jpg'),(31,'20260510','$2a$10$ollJxII75SAQz7iOjWunjelocrh93JBx3vEkebmWSb.fpA3HMcEN2','Test0510',1,'18894456743','3559232235@qq.com',1,'2026-05-10 15:04:12','男','http://localhost:8080/imgs/user_e783fe4b-3a6e-47d1-b31c-503d6280a8ea.jpg'),(32,'2026001','$2a$10$hfQPEIazKJJZp37wp3CygesWDoOTIHy.nvdMQAuooGn/P8PJgvYBi','Test001',1,'18894456743','3559232235@qq.com',1,'2026-05-10 16:01:09','男','http://localhost:8080/imgs/user_dff65253-4200-4e6f-9f80-43641934a1fa.jpg'),(33,'2026002','$2a$10$r3Gk.iFN7ImMPYChYZRo5e4Y7fGO7nKMVq573CDJ4IeJoPK3GI5KG','qq',1,'','',1,'2026-05-10 16:09:20','男',NULL),(34,'2026003','$2a$10$F1pS7niftFcK4JtRusstUuJvGfuyU/frjDCv4QqGqbyK2YXhvwQSy','wwwr',1,'18894456743','3559232235@qq.com',1,'2026-05-10 16:12:31','男','http://localhost:8080/imgs/user_abb81c8f-a38e-41c9-88ba-0e0836a632b6.jpg'),(35,'2026004','$2a$10$/7DHaJA9./WQcpaDNkue4eweBzEB0Iz3nBrcqX4Za6CnA/wQzMYLa','test3',1,'18894456743','zhaowendao2279@gmail.com',1,'2026-05-10 16:18:29','男',NULL),(36,'20260515','$2a$10$PLBxC/XTHGaVjY0GbdquUO5pYDpIAA6VIzl4eqBanW/MklJ9Bbjqy','AAA',1,'18894456743','1514055091@qq.com',1,'2026-05-15 09:12:08','男',NULL),(37,'2026006','$2a$10$K2qOjpBqF4MQLiTANsADxePx5wlCWWkUMDesgOLAlSUX6wqYeLNES','Test1',1,'18894456743','1514055091@qq.com',1,'2026-05-15 10:22:44','男',NULL);
/*!40000 ALTER TABLE `user` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping routines for database 'school_backend_manage'
--
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-06-01 14:45:14
