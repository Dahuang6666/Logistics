# 宿舍管理系统API文档

## 1. 学生控制器 (StudentController)

### 1.1 换宿相关接口

#### 学生提交换宿申请
- 接口说明：学生提交换宿申请
- 路径：/school/student/application
- 请求方式：POST
- 请求体：
```json
{
  "studentNo": "学号",
  "currentDormitoryId": 1,
  "targetBuildingId": 2,
  "targetDormitoryNo": "301",
  "reason": "换宿原因"
}
```

#### 首次登录信息填写页面
- 接口说明：首次登录信息填写页面
- 路径：/school/student/firstLoginInfo
- 请求方式：GET
- 参数：userNo (String)

#### 根据性别获取可选宿舍楼列表
- 接口说明：根据性别获取可选宿舍楼列表
- 路径：/school/student/getAvailableBuildings
- 请求方式：GET
- 参数：userNo (String)

#### 根据楼号获取可用宿舍列表
- 接口说明：根据楼号获取可用宿舍列表
- 路径：/school/student/getAvailableDorms
- 请求方式：GET
- 参数：buildingId (Integer)

#### 提交首次宿舍信息
- 接口说明：提交首次宿舍信息
- 路径：/school/student/submitFirstDormInfo
- 请求方式：POST
- 请求体：
```json
{
  "userNo": "学号",
  "buildingId": 1,
  "dormitoryNo": "301"
}
```

#### 获取变更宿舍申请进度
- 接口说明：获取变更宿舍申请进度
- 路径：/school/student/getMyDormApplications
- 请求方式：GET
- 参数：studentNo (String)

#### 撤销换宿申请
- 接口说明：撤销换宿申请
- 路径：/school/student/cancelDormApplication
- 请求方式：PUT
- 参数：applicationId (Integer), studentNo (String)

### 1.2 报修相关接口

#### 学生报修申请
- 接口说明：学生报修申请
- 路径：/school/student/submitRepair
- 请求方式：POST
- 请求体：
```json
{
  "userNo": "学号",
  "dormitoryId": 1,
  "description": "报修描述",
  "imageUrl": "图片URL"
}
```

#### 上传报修图片
- 接口说明：上传报修图片
- 路径：/school/student/uploadAvatar
- 请求方式：POST
- 参数：file (MultipartFile)

#### 获取用户所有申请
- 接口说明：获取用户所有申请
- 路径：/school/student/myRepairs
- 请求方式：GET
- 参数：userNo (String), pageNum (Integer, 默认1), pageSize (Integer, 默认10), status (String, 可选)

#### 取消报修申请
- 接口说明：取消报修申请
- 路径：/school/student/cancelRepair
- 请求方式：PUT
- 参数：repairId (Integer), userNo (String)

### 1.3 公告相关接口

#### 公告查看
- 接口说明：公告查看
- 路径：/school/student/announcementList
- 请求方式：GET
- 参数：pageNum (int, 默认1), pageSize (int, 默认10), priority (Integer, 可选), keyword (String, 可选)

### 1.4 建议相关接口

#### 提交建议
- 接口说明：提交建议
- 路径：/school/student/submitSuggest
- 请求方式：POST
- 请求体：
```json
{
  "userNo": "学号",
  "content": "建议内容",
  "type": "建议类型"
}
```

#### 获取建议
- 接口说明：获取建议
- 路径：/school/student/mySuggestList
- 请求方式：GET
- 参数：userNo (String)

### 1.5 个人信息相关接口

#### 获取学生个人信息
- 接口说明：获取学生个人信息
- 路径：/school/student/getStudentProfile
- 请求方式：GET
- 参数：userNo (String)

#### 根据学号获取宿舍ID
- 接口说明：根据学号获取宿舍ID
- 路径：/school/student/getDormitoryId
- 请求方式：GET
- 参数：userNo (String)

#### 根据学号获取宿舍信息
- 接口说明：根据学号获取宿舍信息
- 路径：/school/student/getCurrentDormInfo
- 请求方式：GET
- 参数：studentNo (String)

#### 根据宿舍ID获取宿舍名称
- 接口说明：根据宿舍ID获取宿舍名称
- 路径：/school/student/getDormNameById
- 请求方式：GET
- 参数：dormitoryId (Integer)

## 2. 管理员控制器 (AdminController)

### 2.1 用户管理接口

#### 创建用户
- 接口说明：创建用户
- 路径：/school/admin/createUser
- 请求方式：POST
- 请求体：
```json
{
  "userNo": "用户编号",
  "username": "用户名",
  "password": "密码",
  "gender": "性别",
  "email": "邮箱",
  "role": "角色"
}
```

#### 重置密码
- 接口说明：重置密码
- 路径：/school/admin/resetPassword
- 请求方式：POST
- 请求体："用户编号"

#### 更新用户信息
- 接口说明：更新用户信息
- 路径：/school/admin/updateUser
- 请求方式：PUT
- 请求体：
```json
{
  "id": 1,
  "userNo": "用户编号",
  "username": "用户名",
  "password": "密码",
  "gender": "性别",
  "email": "邮箱",
  "role": "角色"
}
```

#### 删除用户
- 接口说明：删除用户
- 路径：/school/admin/deleteUser/{id}
- 请求方式：DELETE
- 参数：id (Integer, 路径参数)

#### 根据ID获取用户
- 接口说明：根据ID获取用户
- 路径：/school/admin/{id}
- 请求方式：GET
- 参数：id (Integer, 路径参数)

#### 获取所有用户
- 接口说明：获取所有用户
- 路径：/school/admin/getListUser
- 请求方式：GET
- 参数：无

### 2.2 建议管理接口

#### 获取所有建议
- 接口说明：获取所有建议
- 路径：/school/admin/getListSuggest
- 请求方式：GET
- 参数：无

#### 处理建议
- 接口说明：处理建议
- 路径：/school/admin/handle
- 请求方式：PUT
- 请求体：
```json
{
  "id": 1,
  "status": "处理状态",
  "feedback": "处理反馈"
}
```

#### 删除建议
- 接口说明：删除建议
- 路径：/school/admin/deleteSuggest/{id}
- 请求方式：DELETE
- 参数：id (Integer, 查询参数)

### 2.3 统计相关接口

#### 报修数量统计
- 接口说明：报修数量统计
- 路径：/school/admin/repairCount
- 请求方式：GET
- 参数：无

#### 建议数量统计
- 接口说明：建议数量统计
- 路径：/school/admin/suggestCount
- 请求方式：GET
- 参数：无

#### 按类型统计公告数量
- 接口说明：按类型统计公告数量
- 路径：/school/admin/announcement/typeDistribution
- 请求方式：GET
- 参数：无

#### 各楼宿舍入住率
- 接口说明：各楼宿舍入住率
- 路径：/school/admin/dormitory/occupancy
- 请求方式：GET
- 参数：无

#### 报修状态分布
- 接口说明：报修状态分布
- 路径：/school/admin/repair/statusDistribution
- 请求方式：GET
- 参数：无

#### 用户性别比例
- 接口说明：用户性别比例
- 路径：/school/admin/user/genderDistribution
- 请求方式：GET
- 参数：无

#### 宿舍申请数量统计
- 接口说明：宿舍申请数量统计
- 路径：/school/admin/dormCountByStatus
- 请求方式：GET
- 参数：无

### 2.4 宿舍楼管理接口

#### 添加宿舍楼
- 接口说明：添加宿舍楼
- 路径：/school/admin/build
- 请求方式：POST
- 请求体：
```json
{
  "name": "宿舍楼名称",
  "gender": "性别",
  "totalFloors": 6
}
```

#### 获取宿舍楼列表
- 接口说明：获取宿舍楼列表
- 路径：/school/admin/build/list
- 请求方式：GET
- 参数：Build (查询参数)

#### 删除宿舍楼
- 接口说明：删除宿舍楼
- 路径：/school/admin/build/{id}
- 请求方式：DELETE
- 参数：id (Integer, 路径参数)

### 2.5 宿舍管理接口

#### 获取宿舍列表
- 接口说明：获取宿舍列表
- 路径：/school/admin/dorm/list
- 请求方式：GET
- 参数：Dormitory (查询参数)

#### 更新宿舍床位数
- 接口说明：更新宿舍床位数
- 路径：/school/admin/dorm/updateBeds
- 请求方式：PUT
- 参数：id (Integer), amount (Integer)

#### 新增宿舍
- 接口说明：新增宿舍
- 路径：/school/admin/dorm
- 请求方式：POST
- 请求体：
```json
{
  "buildingId": 1,
  "dormitoryNo": "301",
  "capacity": 4,
  "availableBeds": 4
}
```

#### 删除宿舍
- 接口说明：删除宿舍
- 路径：/school/admin/dorm/{id}
- 请求方式：DELETE
- 参数：id (Integer, 路径参数)

## 3. 用户控制器 (UserController)

### 3.1 认证相关接口

#### 注册
- 接口说明：注册
- 路径：/school/user/register
- 请求方式：POST
- 请求体：
```json
{
  "userNo": "用户编号",
  "username": "用户名",
  "password": "密码",
  "gender": "性别",
  "email": "邮箱"
}
```

#### 登录
- 接口说明：登录
- 路径：/school/user/login
- 请求方式：POST
- 请求体：
```json
{
  "userNo": "用户编号",
  "password": "密码"
}
```

#### 修改用户信息
- 接口说明：修改用户信息
- 路径：/school/user/updateUserInfo
- 请求方式：PUT
- 请求体：
```json
{
  "userNo": "用户编号",
  "username": "用户名",
  "password": "密码",
  "gender": "性别",
  "email": "邮箱"
}
```

### 3.2 头像相关接口

#### 上传头像
- 接口说明：上传头像
- 路径：/school/user/uploadAvatar
- 请求方式：POST
- 参数：userNo (String), file (MultipartFile)

#### 根据学号/工号获取头像
- 接口说明：根据学号/工号获取头像
- 路径：/school/user/avatar
- 请求方式：GET
- 参数：userNo (String)

### 3.3 信息相关接口

#### 获取用户信息
- 接口说明：获取用户信息
- 路径：/school/user/getUserInfo
- 请求方式：GET
- 参数：userNo (String)

#### 获取用户名
- 接口说明：获取用户名
- 路径：/school/user/getUserName
- 请求方式：GET
- 参数：userNo (String)

### 3.4 验证码相关接口

#### 生成验证码
- 接口说明：生成验证码
- 路径：/school/user/captcha
- 请求方式：GET
- 参数：无

#### 验证验证码
- 接口说明：验证验证码
- 路径：/school/user/verifyCaptcha
- 请求方式：POST
- 请求体：
```json
{
  "captchaId": "验证码ID",
  "captchaInput": "用户输入的验证码"
}
```

#### 邮箱发送验证码
- 接口说明：邮箱发送验证码
- 路径：/school/user/sendEmail
- 请求方式：GET
- 参数：userNo (String)

#### 获取邮箱
- 接口说明：获取邮箱
- 路径：/school/user/getEmail
- 请求方式：GET
- 参数：userNo (String)

#### 验证邮箱验证码
- 接口说明：验证邮箱验证码
- 路径：/school/user/verifyEmail
- 请求方式：POST
- 请求体：
```json
{
  "userNo": "用户编号",
  "code": "验证码"
}
```

## 4. 宿舍管理员控制器 (DormAdminController)

### 4.1 换宿申请相关接口

#### 获取待审批所有变更宿舍申请
- 接口说明：获取待审批所有变更宿舍申请
- 路径：/school/dormAdmin/getAllApplication
- 请求方式：GET
- 参数：无

#### 更新变更宿舍申请状态
- 接口说明：更新变更宿舍申请状态，当状态为"已同意"时，会自动处理宿舍分配、床位更新
- 路径：/school/dormAdmin/updateApplicationStatus
- 请求方式：POST
- 请求体：
```json
{
  "applicationId": 1,
  "status": "已同意",
  "approverNo": "admin001",
  "comment": "同意换宿"
}
```

#### 根据状态获取变更宿舍信息
- 接口说明：根据状态获取变更宿舍信息
- 路径：/school/dormAdmin/getApplicationList
- 请求方式：GET
- 参数：status (String, 可选)

#### 获取可用宿舍列表
- 接口说明：获取有剩余床位且状态正常的宿舍列表
- 路径：/school/dormAdmin/getAvailableDorms
- 请求方式：GET
- 参数：buildingId (Integer, 可选) - 宿舍楼ID

#### 根据学生性别获取可用宿舍楼
- 接口说明：根据学生性别获取可用的宿舍楼列表
- 路径：/school/dormAdmin/getAvailableBuildingsByGender
- 请求方式：GET
- 参数：studentNo (String) - 学号

#### 修改申请的目标宿舍
- 接口说明：管理员修改换宿申请的目标宿舍
- 路径：/school/dormAdmin/updateTargetDorm
- 请求方式：POST
- 参数：applicationId (Integer), targetDormId (Integer)

### 4.2 报修相关接口

#### 管理员查看全部报修
- 接口说明：管理员查看全部报修
- 路径：/school/dormAdmin/all
- 请求方式：GET
- 参数：无

#### 管理员更新报修状态
- 接口说明：管理员更新报修状态
- 路径：/school/dormAdmin/updateStatus
- 请求方式：PUT
- 参数：id (Integer), status (String)

#### 管理员删除报修（软删除）
- 接口说明：管理员删除报修（软删除）
- 路径：/school/dormAdmin/delete
- 请求方式：DELETE
- 参数：id (Integer)

### 4.3 公告相关接口

#### 发布新公告
- 接口说明：发布新公告
- 路径：/school/dormAdmin/addAnnouncement
- 请求方式：POST
- 请求体：
```json
{
  "title": "公告标题",
  "content": "公告内容",
  "typeId": 1,
  "priority": 1,
  "publisher": "发布人"
}
```

#### 修改公告
- 接口说明：修改公告
- 路径：/school/dormAdmin/updateAnnouncement
- 请求方式：PUT
- 请求体：
```json
{
  "id": 1,
  "title": "公告标题",
  "content": "公告内容",
  "typeId": 1,
  "priority": 1,
  "publisher": "发布人"
}
```

#### 删除公告
- 接口说明：删除公告
- 路径：/school/dormAdmin/deleteAnnouncement
- 请求方式：DELETE
- 参数：id (Integer)

### 4.4 公告类型相关接口

#### 获取所有公告类型
- 接口说明：获取所有公告类型
- 路径：/school/dormAdmin/getAllTypes
- 请求方式：GET
- 参数：无

#### 新增公告类型
- 接口说明：新增公告类型
- 路径：/school/dormAdmin/addType
- 请求方式：POST
- 请求体：
```json
{
  "name": "类型名称"
}
```

#### 删除公告类型
- 接口说明：删除公告类型
- 路径：/school/dormAdmin/deleteType
- 请求方式：DELETE
- 参数：id (Integer)

#### 修改公告类型
- 接口说明：修改公告类型
- 路径：/school/dormAdmin/updateType
- 请求方式：PUT
- 请求体：
```json
{
  "id": 1,
  "name": "类型名称"
}
```

### 4.5 宿舍信息接口

#### 根据宿舍ID查询宿舍号
- 接口说明：根据宿舍ID查询宿舍号
- 路径：/school/dormAdmin/getDormitoryNoById
- 请求方式：GET
- 参数：dormitoryId (Integer)
