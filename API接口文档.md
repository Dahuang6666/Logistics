# 校园寝室管理系统 API 接口文档

## 基础信息

- **基础URL**: `http://localhost:8080`
- **数据格式**: JSON
- **字符编码**: UTF-8

## 统一响应格式

所有接口统一使用 `Result` 对象作为响应格式：

```json
{
  "code": 1,        
  "msg": "success",
  "data": {}        
}
```

---

## 一、用户模块 (UserController)

**基础路径**: `/school/user`

### 1.1 用户注册

**接口地址**: `POST /school/user/register`

**请求参数**:

```json
{
  "userNo": "string",      
  "password": "string",  
  "username": "string",  
  "role": 1,            
  "phone": "string",       
  "email": "string",    
  "gender": "string"   
}
```

**响应示例**:

```json
{
  "code": 1,
  "msg": "success",
  "data": null
}
```

**错误响应**:

```json
{
  "code": 0,
  "msg": "账号已注册",
  "data": null
}
```

---

### 1.2 用户登录

**接口地址**: `POST /school/user/login`

**请求参数**:

```json
{
  "userNo": "string",     
  "password": "string"   
}
```

**响应示例**:

```json
{
  "code": 1,
  "msg": "success",
  "data": null
}
```

**错误响应**:

```json
{
  "code": 0,
  "msg": "账号或密码错误",
  "data": null
}
```

或

```json
{
  "code": 0,
  "msg": "帐户被禁用",
  "data": null
}
```

---

### 1.3 更新用户信息

**接口地址**: `PUT /school/user/updateUserInfo`

**请求参数**:

```json
{
  "userNo": "string",     
  "password": "string",    
  "username": "string",    
  "phone": "string",       
  "email": "string",       
  "gender": "string"       
}
```

**响应示例**:

```json
{
  "code": 1,
  "msg": "success",
  "data": null
}
```

**错误响应**:

```json
{
  "code": 0,
  "msg": "账号不存在",
  "data": null
}
```

或

```json
{
  "code": 0,
  "msg": "更新用户信息失败",
  "data": null
}
```

---

### 1.4 上传头像

**接口地址**: `POST /school/user/uploadAvatar`

**请求类型**: `multipart/form-data`

**请求参数**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| userNo | string | 是 | 用户账号 |
| file | file | 是 | 头像图片文件 |

**响应示例**:

```json
{
  "code": 1,
  "msg": "success",
  "data": "http://localhost:8080/avatars/xxx.jpg"
}
```

**错误响应**:

```json
{
  "code": 0,
  "msg": "头像上传失败",
  "data": null
}
```

---

### 1.5 获取用户信息

**接口地址**: `GET /school/user/getUserInfo`

**请求参数**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| userNo | string | 是 | 用户账号 |

**响应示例**:

```json
{
  "code": 1,
  "msg": "success",
  "data": {
    "id": 1,
    "userNo": "2021001",
    "username": "张三",
    "role": 1,
    "phone": "13800138000",
    "email": "zhangsan@example.com",
    "gender": "男",
    "avatar": "http://localhost:8080/avatars/xxx.jpg",
    "status": 0
  }
}
```

**错误响应**:

```json
{
  "code": 0,
  "msg": "用户不存在",
  "data": null
}
```

---

### 1.6 获取验证码

**接口地址**: `GET /school/user/captcha`

**响应示例**:

```json
{
  "captcha": "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAA...",
  "captchaId": "550e8400-e29b-41d4-a716-446655440000"
}
```

**说明**: 
- `captcha`: Base64编码的验证码图片
- `captchaId`: 验证码唯一标识，用于后续验证

---

### 1.7 验证验证码

**接口地址**: `POST /school/user/verifyCaptcha`

**请求参数**:

```json
{
  "captchaId": "string",     
  "captchaInput": "string"    
}
```

**响应示例**:

```json
{
  "success": true
}
```

**错误响应**:

```json
{
  "success": false,
  "message": "验证码错误或已失效"
}
```

---

## 二、学生模块 (StudentController)

**基础路径**: `/school/student`

### 2.1 提交换宿申请

**接口地址**: `POST /school/student/application`

**请求参数**:

```json
{
  "studentNo": "string",        
  "currentDormitoryId": 101,     
  "reason": "string"               
}
```

**响应示例**:

```json
{
  "code": 1,
  "msg": "success",
  "data": "提交申请成功"
}
```

**错误响应**:

```json
{
  "code": 0,
  "msg": "提交失败",
  "data": null
}
```

---

### 2.2 首次登录信息填写页面检查

**接口地址**: `GET /school/student/firstLoginInfo`

**请求参数**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| userNo | string | 是 | 用户账号 |

**响应示例**:

```json
true  
```

或

```json
false  
```

---

### 2.3 获取我的换宿申请列表

**接口地址**: `GET /school/student/getMyDormApplications`

**请求参数**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| studentNo | string | 是 | 学号 |

**响应示例**:

```json
{
  "code": 1,
  "msg": "success",
  "data": [
    {
      "id": 1,
      "studentNo": "2021001",
      "currentDormitoryId": 101,
      "targetDormitoryId": 102,
      "reason": "希望换到更安静的宿舍",
      "status": "PENDING",
      "applicationTime": "2024-01-01T10:00:00",
      "approvalTime": null
    }
  ]
}
```

**状态说明**:
- `PENDING`: 待审批
- `APPROVED`: 已同意
- `REJECTED`: 已拒绝

---

### 2.4 提交报修申请

**接口地址**: `POST /school/student/submitRepair`

**请求参数**:

```json
{
  "userNo": "string",        
  "dormitoryId": 101,        
  "content": "string",       
  "imageUrl": "string",    
  "repairType": "string",   
  "urgencyLevel": "string"  
}
```

**响应示例**:

```json
{
  "code": 1,
  "msg": "success",
  "data": null
}
```

**错误响应**:

```json
{
  "code": 0,
  "msg": "提交失败",
  "data": null
}
```

---

### 2.5 查询我的报修单

**接口地址**: `GET /school/student/myRepairs`

**请求参数**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| userNo | string | 是 | 用户账号 |

**响应示例**:

```json
{
  "code": 1,
  "msg": "success",
  "data": [
    {
      "id": 1,
      "userNo": "2021001",
      "dormitoryId": 101,
      "content": "水龙头漏水",
      "imageUrl": "http://localhost:8080/images/xxx.jpg",
      "repairType": "水电",
      "urgencyLevel": "高",
      "status": "待处理",
      "createTime": "2024-01-01T10:00:00"
    }
  ]
}
```

---

### 2.6 获取公告列表

**接口地址**: `GET /school/student/announcementList`

**请求参数**:

| 参数名 | 类型 | 必填 | 默认值 | 说明 |
|--------|------|------|--------|------|
| pageNum | int | 否 | 1 | 页码 |
| pageSize | int | 否 | 10 | 每页数量 |

**响应示例**:

```json
{
  "code": 1,
  "msg": "success",
  "data": {
    "list": [
      {
        "id": 1,
        "title": "关于宿舍管理的通知",
        "content": "通知内容...",
        "publisher": "宿舍管理员",
        "publishDate": "2024-01-01T10:00:00"
      }
    ],
    "total": 100
  }
}
```

---

### 2.7 获取公告详情

**接口地址**: `GET /school/student/detail`

**请求参数**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| id | int | 是 | 公告ID |

**响应示例**:

```json
{
  "code": 1,
  "msg": "success",
  "data": {
    "id": 1,
    "title": "关于宿舍管理的通知",
    "content": "详细通知内容...",
    "publisher": "宿舍管理员",
    "publishDate": "2024-01-01T10:00:00"
  }
}
```

**错误响应**:

```json
{
  "code": 0,
  "msg": "公告不存在或已删除",
  "data": null
}
```

---

### 2.8 提交投诉建议

**接口地址**: `POST /school/student/submitSuggest`

**请求参数**:

```json
{
  "userNo": "string",       
  "title": "string",        
  "content": "string",       
  "type": "string"
}
```

**响应示例**:

```json
{
  "code": 1,
  "msg": "success",
  "data": null
}
```

**错误响应**:

```json
{
  "code": 0,
  "msg": "提交失败",
  "data": null
}
```

---

### 2.9 获取我的投诉建议列表

**接口地址**: `GET /school/student/mySuggestList`

**请求参数**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| userNo | string | 是 | 用户账号 |

**响应示例**:

```json
{
  "code": 1,
  "msg": "success",
  "data": [
    {
      "id": 1,
      "userNo": "2021001",
      "title": "关于宿舍卫生的建议",
      "content": "建议内容...",
      "type": "建议",
      "status": "待处理",
      "createTime": "2024-01-01T10:00:00"
    }
  ]
}
```

---

## 三、管理员模块 (AdminController)

**基础路径**: `/school/admin`

### 3.1 创建用户

**接口地址**: `POST /school/admin/createUser`

**请求参数**:

```json
{
  "userNo": "string",      
  "password": "string",    
  "username": "string",   
  "role": 1,             
  "phone": "string",       
  "email": "string",       
  "gender": "string",      
  "status": 0             
}
```

**响应示例**:

```json
{
  "code": 1,
  "msg": "success",
  "data": null
}
```

---

### 3.2 更新用户

**接口地址**: `PUT /school/admin/updateUser`

**请求参数**:

```json
{
  "id": 1,           
  "userNo": "string",     
  "username": "string",  
  "role": 1,             
  "phone": "string",    
  "email": "string",      
  "gender": "string",    
  "status": 0          
}
```

**响应示例**:

```json
{
  "code": 1,
  "msg": "success",
  "data": null
}
```

---

### 3.3 删除用户

**接口地址**: `DELETE /school/admin/deleteUser/{id}`

**路径参数**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| id | int | 是 | 用户ID |

**响应示例**:

```json
{
  "code": 1,
  "msg": "success",
  "data": null
}
```

---

### 3.4 根据ID获取用户

**接口地址**: `GET /school/admin/{id}`

**路径参数**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| id | int | 是 | 用户ID |

**响应示例**:

```json
{
  "id": 1,
  "userNo": "2021001",
  "username": "张三",
  "role": 1,
  "phone": "13800138000",
  "email": "zhangsan@example.com",
  "gender": "男",
  "status": 0
}
```

---

### 3.5 获取所有用户列表

**接口地址**: `GET /school/admin/getListUser`

**响应示例**:

```json
{
  "code": 1,
  "msg": "success",
  "data": [
    {
      "id": 1,
      "userNo": "2021001",
      "username": "张三",
      "role": 1,
      "phone": "13800138000",
      "email": "zhangsan@example.com",
      "gender": "男",
      "status": 0
    }
  ]
}
```

---

### 3.6 获取所有投诉建议

**接口地址**: `GET /school/admin/getListSuggest`

**响应示例**:

```json
{
  "code": 1,
  "msg": "success",
  "data": [
    {
      "id": 1,
      "userNo": "2021001",
      "title": "关于宿舍卫生的建议",
      "content": "建议内容...",
      "type": "建议",
      "status": "待处理",
      "handleResult": null,
      "createTime": "2024-01-01T10:00:00"
    }
  ]
}
```

---

### 3.7 处理投诉建议

**接口地址**: `PUT /school/admin/handle`

**请求参数**:

```json
{
  "id": 1,
  "status": "已处理",
  "handleResult": "string"
}
```

**响应示例**:

```json
{
  "code": 1,
  "msg": "success",
  "data": null
}
```

**错误响应**:

```json
{
  "code": 0,
  "msg": "处理失败",
  "data": null
}
```

---

### 3.8 删除投诉建议

**接口地址**: `DELETE /school/admin/deleteSuggest/{id}`

**请求参数**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| id | int | 是 | 投诉建议ID |

**响应示例**:

```json
{
  "code": 1,
  "msg": "success",
  "data": null
}
```

**错误响应**:

```json
{
  "code": 0,
  "msg": "删除失败",
  "data": null
}
```

---

### 3.9 报修数量统计

**接口地址**: `GET /school/admin/repairCount`

**响应示例**:

```json
{
  "code": 1,
  "msg": "success",
  "data": {
    "待处理": 10,
    "处理中": 5,
    "已完成": 20,
    "已取消": 2
  }
}
```

---

### 3.10 建议数量统计

**接口地址**: `GET /school/admin/suggestCount`

**响应示例**:

```json
{
  "code": 1,
  "msg": "success",
  "data": [
    {
      "status": "待处理",
      "count": 5
    },
    {
      "status": "已处理",
      "count": 10
    }
  ]
}
```

---

### 3.11 宿舍申请数量统计

**接口地址**: `GET /school/admin/dormCountByStatus`

**响应示例**:

```json
{
  "code": 1,
  "msg": "success",
  "data": [
    {
      "status": "PENDING",
      "count": 5
    },
    {
      "status": "APPROVED",
      "count": 10
    },
    {
      "status": "REJECTED",
      "count": 2
    }
  ]
}
```

---

## 四、宿舍管理员模块 (DormAdminController)

**基础路径**: `/school/dormAdmin`

### 4.1 获取所有待审批申请

**接口地址**: `GET /school/dormAdmin/getAllApplication`

**响应示例**:

```json
{
  "code": 1,
  "msg": "success",
  "data": [
    {
      "id": 1,
      "studentNo": "2021001",
      "studentName": "张三",
      "currentDormitoryId": 101,
      "currentDormitoryName": "1号楼101",
      "targetDormitoryId": 102,
      "targetDormitoryName": "1号楼102",
      "reason": "希望换到更安静的宿舍",
      "status": "PENDING",
      "applicationTime": "2024-01-01T10:00:00"
    }
  ]
}
```

---

### 4.2 AI生成公告

**接口地址**: `POST /school/dormAdmin/getchat`

**请求参数**:

```json
{
  "topic": "string",         
  "publisher": "string",      
  "publishDate": "string",   
  "summary": "string"        
}
```

**响应示例**:

```json
"生成的公告内容文本..."
```

---

### 4.3 更新申请状态

**接口地址**: `POST /school/dormAdmin/updateApplicationStatus`

**请求参数**:

```json
{
  "id": 1,                   
  "status": "APPROVED"        
}
```

**响应示例**:

```json
{
  "code": 1,
  "msg": "success",
  "data": "状态更新成功"
}
```

**错误响应**:

```json
{
  "code": 0,
  "msg": "状态更新失败",
  "data": null
}
```

---

### 4.4 根据状态获取申请列表

**接口地址**: `GET /school/dormAdmin/getApplicationList`

**请求参数**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| status | string | 否 | 状态：PENDING/APPROVED/REJECTED，不传则返回所有 |

**响应示例**:

```json
{
  "code": 1,
  "msg": "success",
  "data": [
    {
      "id": 1,
      "studentNo": "2021001",
      "studentName": "张三",
      "currentDormitoryId": 101,
      "currentDormitoryName": "1号楼101",
      "targetDormitoryId": 102,
      "targetDormitoryName": "1号楼102",
      "reason": "希望换到更安静的宿舍",
      "status": "APPROVED",
      "applicationTime": "2024-01-01T10:00:00",
      "approvalTime": "2024-01-02T10:00:00"
    }
  ]
}
```

---

### 4.5 查看所有报修单

**接口地址**: `GET /school/dormAdmin/all`

**响应示例**:

```json
{
  "code": 1,
  "msg": "success",
  "data": [
    {
      "id": 1,
      "userNo": "2021001",
      "dormitoryId": 101,
      "content": "水龙头漏水",
      "imageUrl": "http://localhost:8080/images/xxx.jpg",
      "repairType": "水电",
      "urgencyLevel": "高",
      "status": "待处理",
      "createTime": "2024-01-01T10:00:00"
    }
  ]
}
```

---

### 4.6 更新报修状态

**接口地址**: `PUT /school/dormAdmin/updateStatus`

**请求参数**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| id | int | 是 | 报修单ID |
| status | string | 是 | 新状态 |

**响应示例**:

```json
{
  "code": 1,
  "msg": "success",
  "data": null
}
```

**错误响应**:

```json
{
  "code": 0,
  "msg": "状态更新失败",
  "data": null
}
```

---

### 4.7 删除报修单（软删除）

**接口地址**: `DELETE /school/dormAdmin/delete`

**请求参数**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| id | int | 是 | 报修单ID |

**响应示例**:

```json
{
  "code": 1,
  "msg": "success",
  "data": null
}
```

**错误响应**:

```json
{
  "code": 0,
  "msg": "删除失败",
  "data": null
}
```

---

### 4.8 查看所有公告

**接口地址**: `GET /school/dormAdmin/listAnnouncement`

**响应示例**:

```json
{
  "code": 1,
  "msg": "success",
  "data": [
    {
      "id": 1,
      "title": "关于宿舍管理的通知",
      "content": "通知内容...",
      "publisher": "宿舍管理员",
      "publishDate": "2024-01-01T10:00:00"
    }
  ]
}
```

---

### 4.9 修改公告

**接口地址**: `PUT /school/dormAdmin/updateAnnouncement`

**请求参数**:

```json
{
  "id": 1,                    
  "title": "string",         
  "content": "string",      
  "publisher": "string"     
}
```

**响应示例**:

```json
{
  "code": 1,
  "msg": "success",
  "data": null
}
```

**错误响应**:

```json
{
  "code": 0,
  "msg": "公告修改失败",
  "data": null
}
```

---

### 4.10 删除公告

**接口地址**: `DELETE /school/dormAdmin/deleteAnnouncement`

**请求参数**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| id | int | 是 | 公告ID |

**响应示例**:

```json
{
  "code": 1,
  "msg": "success",
  "data": null
}
```

**错误响应**:

```json
{
  "code": 0,
  "msg": "删除失败",
  "data": null
}
```

---

## 五、错误码说明

| 错误码 | 说明 |
|--------|------|
| 1 | 成功 |
| 0 | 失败 |

## 六、注意事项

1. 所有接口均支持跨域请求（CORS）
2. 时间格式统一使用 ISO 8601 格式：`yyyy-MM-ddTHH:mm:ss`
3. 文件上传接口使用 `multipart/form-data` 格式
4. 密码在传输和存储时均会进行加密处理
5. 验证码有效期为单次使用，验证成功后立即失效
6. 角色说明：
   - 1: 学生
   - 2: 宿舍管理员
   - 3: 系统管理员
7. 申请状态说明：
   - PENDING: 待审批
   - APPROVED: 已同意
   - REJECTED: 已拒绝

---

**文档版本**: v1.0  
**最后更新**: 2024-01-01

