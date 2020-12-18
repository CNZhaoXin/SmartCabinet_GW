package com.zk.cabinet.entity;

import java.util.Date;
import java.util.List;

public class ResultApiLogin {
//    {
//        "msg": "操作成功",
//            "code": 200,
//            "token": "eyJhbGciOiJIUzUxMiJ9.eyJsb2dpbl91c2VyX2tleSI6IjViYzlkZGEzLTE4MzgtNDIyYy05NzQxLTUyNDUxMzY5YThkMyJ9.4AkVLkyUlFaQsM1YNXEU7Qspl8C0Zoc3YAHVzbv86BbWqagxTzdWxg41rIHKfqGeVQ6gaq19lL3DKsJENk16Zg"
//    }

    private String msg;
    private int code;
    private Data data;

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getMsg() {
        return msg;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public Data getData() {
        return data;
    }


    public class Data {

        private String token;
        private User user;

        public void setToken(String token) {
            this.token = token;
        }

        public String getToken() {
            return token;
        }

        public void setUser(User user) {
            this.user = user;
        }

        public User getUser() {
            return user;
        }

        public class User {
            private String searchValue;
            private String createBy;
            private Date createTime;
            private String updateBy;
            private String updateTime;
            private String remark;
            private String dataScope;
            private Params params;
            private int userId;
            private int deptId;
            private String userName;
            private String nickName;
            private String userType;
            private String userLevel;
            private String unionid;
            private String openid;
            private String idCard;
            private String email;
            private String phonenumber;
            private String sex;
            private String avatar;
            private String password;
            private String salt;
            private String status;
            private String delFlag;
            private String loginIp;
            private Date loginDate;
            private Dept dept;
            private List<Roles> roles;
            private String roleIds;
            private String postIds;
            private boolean admin;

            public class Params {
            }

            public void setSearchValue(String searchValue) {
                this.searchValue = searchValue;
            }

            public String getSearchValue() {
                return searchValue;
            }

            public void setCreateBy(String createBy) {
                this.createBy = createBy;
            }

            public String getCreateBy() {
                return createBy;
            }

            public void setCreateTime(Date createTime) {
                this.createTime = createTime;
            }

            public Date getCreateTime() {
                return createTime;
            }

            public void setUpdateBy(String updateBy) {
                this.updateBy = updateBy;
            }

            public String getUpdateBy() {
                return updateBy;
            }

            public void setUpdateTime(String updateTime) {
                this.updateTime = updateTime;
            }

            public String getUpdateTime() {
                return updateTime;
            }

            public void setRemark(String remark) {
                this.remark = remark;
            }

            public String getRemark() {
                return remark;
            }

            public void setDataScope(String dataScope) {
                this.dataScope = dataScope;
            }

            public String getDataScope() {
                return dataScope;
            }

            public void setParams(Params params) {
                this.params = params;
            }

            public Params getParams() {
                return params;
            }

            public void setUserId(int userId) {
                this.userId = userId;
            }

            public int getUserId() {
                return userId;
            }

            public void setDeptId(int deptId) {
                this.deptId = deptId;
            }

            public int getDeptId() {
                return deptId;
            }

            public void setUserName(String userName) {
                this.userName = userName;
            }

            public String getUserName() {
                return userName;
            }

            public void setNickName(String nickName) {
                this.nickName = nickName;
            }

            public String getNickName() {
                return nickName;
            }

            public void setUserType(String userType) {
                this.userType = userType;
            }

            public String getUserType() {
                return userType;
            }

            public void setUserLevel(String userLevel) {
                this.userLevel = userLevel;
            }

            public String getUserLevel() {
                return userLevel;
            }

            public void setUnionid(String unionid) {
                this.unionid = unionid;
            }

            public String getUnionid() {
                return unionid;
            }

            public void setOpenid(String openid) {
                this.openid = openid;
            }

            public String getOpenid() {
                return openid;
            }

            public void setIdCard(String idCard) {
                this.idCard = idCard;
            }

            public String getIdCard() {
                return idCard;
            }

            public void setEmail(String email) {
                this.email = email;
            }

            public String getEmail() {
                return email;
            }

            public void setPhonenumber(String phonenumber) {
                this.phonenumber = phonenumber;
            }

            public String getPhonenumber() {
                return phonenumber;
            }

            public void setSex(String sex) {
                this.sex = sex;
            }

            public String getSex() {
                return sex;
            }

            public void setAvatar(String avatar) {
                this.avatar = avatar;
            }

            public String getAvatar() {
                return avatar;
            }

            public void setPassword(String password) {
                this.password = password;
            }

            public String getPassword() {
                return password;
            }

            public void setSalt(String salt) {
                this.salt = salt;
            }

            public String getSalt() {
                return salt;
            }

            public void setStatus(String status) {
                this.status = status;
            }

            public String getStatus() {
                return status;
            }

            public void setDelFlag(String delFlag) {
                this.delFlag = delFlag;
            }

            public String getDelFlag() {
                return delFlag;
            }

            public void setLoginIp(String loginIp) {
                this.loginIp = loginIp;
            }

            public String getLoginIp() {
                return loginIp;
            }

            public void setLoginDate(Date loginDate) {
                this.loginDate = loginDate;
            }

            public Date getLoginDate() {
                return loginDate;
            }

            public void setDept(Dept dept) {
                this.dept = dept;
            }

            public Dept getDept() {
                return dept;
            }

            public void setRoles(List<Roles> roles) {
                this.roles = roles;
            }

            public List<Roles> getRoles() {
                return roles;
            }

            public void setRoleIds(String roleIds) {
                this.roleIds = roleIds;
            }

            public String getRoleIds() {
                return roleIds;
            }

            public void setPostIds(String postIds) {
                this.postIds = postIds;
            }

            public String getPostIds() {
                return postIds;
            }

            public void setAdmin(boolean admin) {
                this.admin = admin;
            }

            public boolean getAdmin() {
                return admin;
            }

            public class Dept {

                private String searchValue;
                private String createBy;
                private String createTime;
                private String updateBy;
                private String updateTime;
                private String remark;
                private String dataScope;
                private Params params;
                private int deptId;
                private int parentId;
                private String ancestors;
                private String deptName;
                private String deptCode;
                private String orderNum;
                private String leader;
                private String phone;
                private String email;
                private String status;
                private String delFlag;
                private String parentName;
                private List<String> children;

                public class Params {

                }

                public void setSearchValue(String searchValue) {
                    this.searchValue = searchValue;
                }

                public String getSearchValue() {
                    return searchValue;
                }

                public void setCreateBy(String createBy) {
                    this.createBy = createBy;
                }

                public String getCreateBy() {
                    return createBy;
                }

                public void setCreateTime(String createTime) {
                    this.createTime = createTime;
                }

                public String getCreateTime() {
                    return createTime;
                }

                public void setUpdateBy(String updateBy) {
                    this.updateBy = updateBy;
                }

                public String getUpdateBy() {
                    return updateBy;
                }

                public void setUpdateTime(String updateTime) {
                    this.updateTime = updateTime;
                }

                public String getUpdateTime() {
                    return updateTime;
                }

                public void setRemark(String remark) {
                    this.remark = remark;
                }

                public String getRemark() {
                    return remark;
                }

                public void setDataScope(String dataScope) {
                    this.dataScope = dataScope;
                }

                public String getDataScope() {
                    return dataScope;
                }

                public void setParams(Params params) {
                    this.params = params;
                }

                public Params getParams() {
                    return params;
                }

                public void setDeptId(int deptId) {
                    this.deptId = deptId;
                }

                public int getDeptId() {
                    return deptId;
                }

                public void setParentId(int parentId) {
                    this.parentId = parentId;
                }

                public int getParentId() {
                    return parentId;
                }

                public void setAncestors(String ancestors) {
                    this.ancestors = ancestors;
                }

                public String getAncestors() {
                    return ancestors;
                }

                public void setDeptName(String deptName) {
                    this.deptName = deptName;
                }

                public String getDeptName() {
                    return deptName;
                }

                public void setDeptCode(String deptCode) {
                    this.deptCode = deptCode;
                }

                public String getDeptCode() {
                    return deptCode;
                }

                public void setOrderNum(String orderNum) {
                    this.orderNum = orderNum;
                }

                public String getOrderNum() {
                    return orderNum;
                }

                public void setLeader(String leader) {
                    this.leader = leader;
                }

                public String getLeader() {
                    return leader;
                }

                public void setPhone(String phone) {
                    this.phone = phone;
                }

                public String getPhone() {
                    return phone;
                }

                public void setEmail(String email) {
                    this.email = email;
                }

                public String getEmail() {
                    return email;
                }

                public void setStatus(String status) {
                    this.status = status;
                }

                public String getStatus() {
                    return status;
                }

                public void setDelFlag(String delFlag) {
                    this.delFlag = delFlag;
                }

                public String getDelFlag() {
                    return delFlag;
                }

                public void setParentName(String parentName) {
                    this.parentName = parentName;
                }

                public String getParentName() {
                    return parentName;
                }

                public void setChildren(List<String> children) {
                    this.children = children;
                }

                public List<String> getChildren() {
                    return children;
                }

            }

            public class Roles {

                private String searchValue;
                private String createBy;
                private String createTime;
                private String updateBy;
                private String updateTime;
                private String remark;
                private String dataScope;
                private Params params;
                private int roleId;
                private String roleName;
                private String roleType;
                private String roleKey;
                private String roleSort;
                private String status;
                private String delFlag;
                private boolean flag;
                private String menuIds;
                private String deptIds;
                private boolean admin;

                public class Params {

                }

                public void setSearchValue(String searchValue) {
                    this.searchValue = searchValue;
                }

                public String getSearchValue() {
                    return searchValue;
                }

                public void setCreateBy(String createBy) {
                    this.createBy = createBy;
                }

                public String getCreateBy() {
                    return createBy;
                }

                public void setCreateTime(String createTime) {
                    this.createTime = createTime;
                }

                public String getCreateTime() {
                    return createTime;
                }

                public void setUpdateBy(String updateBy) {
                    this.updateBy = updateBy;
                }

                public String getUpdateBy() {
                    return updateBy;
                }

                public void setUpdateTime(String updateTime) {
                    this.updateTime = updateTime;
                }

                public String getUpdateTime() {
                    return updateTime;
                }

                public void setRemark(String remark) {
                    this.remark = remark;
                }

                public String getRemark() {
                    return remark;
                }

                public void setDataScope(String dataScope) {
                    this.dataScope = dataScope;
                }

                public String getDataScope() {
                    return dataScope;
                }

                public void setParams(Params params) {
                    this.params = params;
                }

                public Params getParams() {
                    return params;
                }

                public void setRoleId(int roleId) {
                    this.roleId = roleId;
                }

                public int getRoleId() {
                    return roleId;
                }

                public void setRoleName(String roleName) {
                    this.roleName = roleName;
                }

                public String getRoleName() {
                    return roleName;
                }

                public void setRoleType(String roleType) {
                    this.roleType = roleType;
                }

                public String getRoleType() {
                    return roleType;
                }

                public void setRoleKey(String roleKey) {
                    this.roleKey = roleKey;
                }

                public String getRoleKey() {
                    return roleKey;
                }

                public void setRoleSort(String roleSort) {
                    this.roleSort = roleSort;
                }

                public String getRoleSort() {
                    return roleSort;
                }

                public void setStatus(String status) {
                    this.status = status;
                }

                public String getStatus() {
                    return status;
                }

                public void setDelFlag(String delFlag) {
                    this.delFlag = delFlag;
                }

                public String getDelFlag() {
                    return delFlag;
                }

                public void setFlag(boolean flag) {
                    this.flag = flag;
                }

                public boolean getFlag() {
                    return flag;
                }

                public void setMenuIds(String menuIds) {
                    this.menuIds = menuIds;
                }

                public String getMenuIds() {
                    return menuIds;
                }

                public void setDeptIds(String deptIds) {
                    this.deptIds = deptIds;
                }

                public String getDeptIds() {
                    return deptIds;
                }

                public void setAdmin(boolean admin) {
                    this.admin = admin;
                }

                public boolean getAdmin() {
                    return admin;
                }

            }
        }

    }

}
