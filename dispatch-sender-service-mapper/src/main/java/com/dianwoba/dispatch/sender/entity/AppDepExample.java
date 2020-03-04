package com.dianwoba.dispatch.sender.entity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AppDepExample {
    protected String orderByClause;

    protected boolean distinct;

    protected List<Criteria> oredCriteria;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database table app_dep
     *
     * @mbg.generated
     * @project https://github.com/itfsw/mybatis-generator-plugin
     */
    protected Integer offset;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database table app_dep
     *
     * @mbg.generated
     * @project https://github.com/itfsw/mybatis-generator-plugin
     */
    protected Integer rows;

    public AppDepExample() {
        oredCriteria = new ArrayList<Criteria>();
    }

    public void setOrderByClause(String orderByClause) {
        this.orderByClause = orderByClause;
    }

    public String getOrderByClause() {
        return orderByClause;
    }

    public void setDistinct(boolean distinct) {
        this.distinct = distinct;
    }

    public boolean isDistinct() {
        return distinct;
    }

    public List<Criteria> getOredCriteria() {
        return oredCriteria;
    }

    public void or(Criteria criteria) {
        oredCriteria.add(criteria);
    }

    public Criteria or() {
        Criteria criteria = createCriteriaInternal();
        oredCriteria.add(criteria);
        return criteria;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table app_dep
     *
     * @mbg.generated
     * @project https://github.com/itfsw/mybatis-generator-plugin
     */
    public AppDepExample orderBy(String orderByClause) {
        this.setOrderByClause(orderByClause);
        return this;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table app_dep
     *
     * @mbg.generated
     * @project https://github.com/itfsw/mybatis-generator-plugin
     */
    public AppDepExample orderBy(String ... orderByClauses) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < orderByClauses.length; i++) {
            sb.append(orderByClauses[i]);
            if (i < orderByClauses.length - 1) {
                sb.append(" , ");
            }
        }
        this.setOrderByClause(sb.toString());
        return this;
    }

    public Criteria createCriteria() {
        Criteria criteria = createCriteriaInternal();
        if (oredCriteria.size() == 0) {
            oredCriteria.add(criteria);
        }
        return criteria;
    }

    protected Criteria createCriteriaInternal() {
        Criteria criteria = new Criteria(this);
        return criteria;
    }

    public void clear() {
        oredCriteria.clear();
        orderByClause = null;
        distinct = false;
        rows = null;
        offset = null;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table app_dep
     *
     * @mbg.generated
     * @project https://github.com/itfsw/mybatis-generator-plugin
     */
    public void setOffset(Integer offset) {
        this.offset = offset;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table app_dep
     *
     * @mbg.generated
     * @project https://github.com/itfsw/mybatis-generator-plugin
     */
    public Integer getOffset() {
        return this.offset;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table app_dep
     *
     * @mbg.generated
     * @project https://github.com/itfsw/mybatis-generator-plugin
     */
    public void setRows(Integer rows) {
        this.rows = rows;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table app_dep
     *
     * @mbg.generated
     * @project https://github.com/itfsw/mybatis-generator-plugin
     */
    public Integer getRows() {
        return this.rows;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table app_dep
     *
     * @mbg.generated
     * @project https://github.com/itfsw/mybatis-generator-plugin
     */
    public AppDepExample limit(Integer rows) {
        this.rows = rows;
        return this;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table app_dep
     *
     * @mbg.generated
     * @project https://github.com/itfsw/mybatis-generator-plugin
     */
    public AppDepExample limit(Integer offset, Integer rows) {
        this.offset = offset;
        this.rows = rows;
        return this;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table app_dep
     *
     * @mbg.generated
     * @project https://github.com/itfsw/mybatis-generator-plugin
     */
    public AppDepExample page(Integer page, Integer pageSize) {
        this.offset = page * pageSize;
        this.rows = pageSize;
        return this;
    }

    protected abstract static class GeneratedCriteria {
        protected List<Criterion> criteria;

        protected GeneratedCriteria() {
            super();
            criteria = new ArrayList<Criterion>();
        }

        public boolean isValid() {
            return criteria.size() > 0;
        }

        public List<Criterion> getAllCriteria() {
            return criteria;
        }

        public List<Criterion> getCriteria() {
            return criteria;
        }

        protected void addCriterion(String condition) {
            if (condition == null) {
                throw new RuntimeException("Value for condition cannot be null");
            }
            criteria.add(new Criterion(condition));
        }

        protected void addCriterion(String condition, Object value, String property) {
            if (value == null) {
                throw new RuntimeException("Value for " + property + " cannot be null");
            }
            criteria.add(new Criterion(condition, value));
        }

        protected void addCriterion(String condition, Object value1, Object value2, String property) {
            if (value1 == null || value2 == null) {
                throw new RuntimeException("Between values for " + property + " cannot be null");
            }
            criteria.add(new Criterion(condition, value1, value2));
        }

        public Criteria andIdIsNull() {
            addCriterion("id is null");
            return (Criteria) this;
        }

        public Criteria andIdIsNotNull() {
            addCriterion("id is not null");
            return (Criteria) this;
        }

        public Criteria andIdEqualTo(Long value) {
            addCriterion("id =", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdNotEqualTo(Long value) {
            addCriterion("id <>", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdGreaterThan(Long value) {
            addCriterion("id >", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdGreaterThanOrEqualTo(Long value) {
            addCriterion("id >=", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdLessThan(Long value) {
            addCriterion("id <", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdLessThanOrEqualTo(Long value) {
            addCriterion("id <=", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdIn(List<Long> values) {
            addCriterion("id in", values, "id");
            return (Criteria) this;
        }

        public Criteria andIdNotIn(List<Long> values) {
            addCriterion("id not in", values, "id");
            return (Criteria) this;
        }

        public Criteria andIdBetween(Long value1, Long value2) {
            addCriterion("id between", value1, value2, "id");
            return (Criteria) this;
        }

        public Criteria andIdNotBetween(Long value1, Long value2) {
            addCriterion("id not between", value1, value2, "id");
            return (Criteria) this;
        }

        public Criteria andAppNameIsNull() {
            addCriterion("app_name is null");
            return (Criteria) this;
        }

        public Criteria andAppNameIsNotNull() {
            addCriterion("app_name is not null");
            return (Criteria) this;
        }

        public Criteria andAppNameEqualTo(String value) {
            addCriterion("app_name =", value, "appName");
            return (Criteria) this;
        }

        public Criteria andAppNameNotEqualTo(String value) {
            addCriterion("app_name <>", value, "appName");
            return (Criteria) this;
        }

        public Criteria andAppNameGreaterThan(String value) {
            addCriterion("app_name >", value, "appName");
            return (Criteria) this;
        }

        public Criteria andAppNameGreaterThanOrEqualTo(String value) {
            addCriterion("app_name >=", value, "appName");
            return (Criteria) this;
        }

        public Criteria andAppNameLessThan(String value) {
            addCriterion("app_name <", value, "appName");
            return (Criteria) this;
        }

        public Criteria andAppNameLessThanOrEqualTo(String value) {
            addCriterion("app_name <=", value, "appName");
            return (Criteria) this;
        }

        public Criteria andAppNameLike(String value) {
            addCriterion("app_name like", value, "appName");
            return (Criteria) this;
        }

        public Criteria andAppNameNotLike(String value) {
            addCriterion("app_name not like", value, "appName");
            return (Criteria) this;
        }

        public Criteria andAppNameIn(List<String> values) {
            addCriterion("app_name in", values, "appName");
            return (Criteria) this;
        }

        public Criteria andAppNameNotIn(List<String> values) {
            addCriterion("app_name not in", values, "appName");
            return (Criteria) this;
        }

        public Criteria andAppNameBetween(String value1, String value2) {
            addCriterion("app_name between", value1, value2, "appName");
            return (Criteria) this;
        }

        public Criteria andAppNameNotBetween(String value1, String value2) {
            addCriterion("app_name not between", value1, value2, "appName");
            return (Criteria) this;
        }

        public Criteria andOwnersDepIdIsNull() {
            addCriterion("owners_dep_id is null");
            return (Criteria) this;
        }

        public Criteria andOwnersDepIdIsNotNull() {
            addCriterion("owners_dep_id is not null");
            return (Criteria) this;
        }

        public Criteria andOwnersDepIdEqualTo(String value) {
            addCriterion("owners_dep_id =", value, "ownersDepId");
            return (Criteria) this;
        }

        public Criteria andOwnersDepIdNotEqualTo(String value) {
            addCriterion("owners_dep_id <>", value, "ownersDepId");
            return (Criteria) this;
        }

        public Criteria andOwnersDepIdGreaterThan(String value) {
            addCriterion("owners_dep_id >", value, "ownersDepId");
            return (Criteria) this;
        }

        public Criteria andOwnersDepIdGreaterThanOrEqualTo(String value) {
            addCriterion("owners_dep_id >=", value, "ownersDepId");
            return (Criteria) this;
        }

        public Criteria andOwnersDepIdLessThan(String value) {
            addCriterion("owners_dep_id <", value, "ownersDepId");
            return (Criteria) this;
        }

        public Criteria andOwnersDepIdLessThanOrEqualTo(String value) {
            addCriterion("owners_dep_id <=", value, "ownersDepId");
            return (Criteria) this;
        }

        public Criteria andOwnersDepIdLike(String value) {
            addCriterion("owners_dep_id like", value, "ownersDepId");
            return (Criteria) this;
        }

        public Criteria andOwnersDepIdNotLike(String value) {
            addCriterion("owners_dep_id not like", value, "ownersDepId");
            return (Criteria) this;
        }

        public Criteria andOwnersDepIdIn(List<String> values) {
            addCriterion("owners_dep_id in", values, "ownersDepId");
            return (Criteria) this;
        }

        public Criteria andOwnersDepIdNotIn(List<String> values) {
            addCriterion("owners_dep_id not in", values, "ownersDepId");
            return (Criteria) this;
        }

        public Criteria andOwnersDepIdBetween(String value1, String value2) {
            addCriterion("owners_dep_id between", value1, value2, "ownersDepId");
            return (Criteria) this;
        }

        public Criteria andOwnersDepIdNotBetween(String value1, String value2) {
            addCriterion("owners_dep_id not between", value1, value2, "ownersDepId");
            return (Criteria) this;
        }

        public Criteria andOwnersPhoneIsNull() {
            addCriterion("owners_phone is null");
            return (Criteria) this;
        }

        public Criteria andOwnersPhoneIsNotNull() {
            addCriterion("owners_phone is not null");
            return (Criteria) this;
        }

        public Criteria andOwnersPhoneEqualTo(String value) {
            addCriterion("owners_phone =", value, "ownersPhone");
            return (Criteria) this;
        }

        public Criteria andOwnersPhoneNotEqualTo(String value) {
            addCriterion("owners_phone <>", value, "ownersPhone");
            return (Criteria) this;
        }

        public Criteria andOwnersPhoneGreaterThan(String value) {
            addCriterion("owners_phone >", value, "ownersPhone");
            return (Criteria) this;
        }

        public Criteria andOwnersPhoneGreaterThanOrEqualTo(String value) {
            addCriterion("owners_phone >=", value, "ownersPhone");
            return (Criteria) this;
        }

        public Criteria andOwnersPhoneLessThan(String value) {
            addCriterion("owners_phone <", value, "ownersPhone");
            return (Criteria) this;
        }

        public Criteria andOwnersPhoneLessThanOrEqualTo(String value) {
            addCriterion("owners_phone <=", value, "ownersPhone");
            return (Criteria) this;
        }

        public Criteria andOwnersPhoneLike(String value) {
            addCriterion("owners_phone like", value, "ownersPhone");
            return (Criteria) this;
        }

        public Criteria andOwnersPhoneNotLike(String value) {
            addCriterion("owners_phone not like", value, "ownersPhone");
            return (Criteria) this;
        }

        public Criteria andOwnersPhoneIn(List<String> values) {
            addCriterion("owners_phone in", values, "ownersPhone");
            return (Criteria) this;
        }

        public Criteria andOwnersPhoneNotIn(List<String> values) {
            addCriterion("owners_phone not in", values, "ownersPhone");
            return (Criteria) this;
        }

        public Criteria andOwnersPhoneBetween(String value1, String value2) {
            addCriterion("owners_phone between", value1, value2, "ownersPhone");
            return (Criteria) this;
        }

        public Criteria andOwnersPhoneNotBetween(String value1, String value2) {
            addCriterion("owners_phone not between", value1, value2, "ownersPhone");
            return (Criteria) this;
        }

        public Criteria andDevelopersDepIdIsNull() {
            addCriterion("developers_dep_id is null");
            return (Criteria) this;
        }

        public Criteria andDevelopersDepIdIsNotNull() {
            addCriterion("developers_dep_id is not null");
            return (Criteria) this;
        }

        public Criteria andDevelopersDepIdEqualTo(String value) {
            addCriterion("developers_dep_id =", value, "developersDepId");
            return (Criteria) this;
        }

        public Criteria andDevelopersDepIdNotEqualTo(String value) {
            addCriterion("developers_dep_id <>", value, "developersDepId");
            return (Criteria) this;
        }

        public Criteria andDevelopersDepIdGreaterThan(String value) {
            addCriterion("developers_dep_id >", value, "developersDepId");
            return (Criteria) this;
        }

        public Criteria andDevelopersDepIdGreaterThanOrEqualTo(String value) {
            addCriterion("developers_dep_id >=", value, "developersDepId");
            return (Criteria) this;
        }

        public Criteria andDevelopersDepIdLessThan(String value) {
            addCriterion("developers_dep_id <", value, "developersDepId");
            return (Criteria) this;
        }

        public Criteria andDevelopersDepIdLessThanOrEqualTo(String value) {
            addCriterion("developers_dep_id <=", value, "developersDepId");
            return (Criteria) this;
        }

        public Criteria andDevelopersDepIdLike(String value) {
            addCriterion("developers_dep_id like", value, "developersDepId");
            return (Criteria) this;
        }

        public Criteria andDevelopersDepIdNotLike(String value) {
            addCriterion("developers_dep_id not like", value, "developersDepId");
            return (Criteria) this;
        }

        public Criteria andDevelopersDepIdIn(List<String> values) {
            addCriterion("developers_dep_id in", values, "developersDepId");
            return (Criteria) this;
        }

        public Criteria andDevelopersDepIdNotIn(List<String> values) {
            addCriterion("developers_dep_id not in", values, "developersDepId");
            return (Criteria) this;
        }

        public Criteria andDevelopersDepIdBetween(String value1, String value2) {
            addCriterion("developers_dep_id between", value1, value2, "developersDepId");
            return (Criteria) this;
        }

        public Criteria andDevelopersDepIdNotBetween(String value1, String value2) {
            addCriterion("developers_dep_id not between", value1, value2, "developersDepId");
            return (Criteria) this;
        }

        public Criteria andDevelopersPhoneIsNull() {
            addCriterion("developers_phone is null");
            return (Criteria) this;
        }

        public Criteria andDevelopersPhoneIsNotNull() {
            addCriterion("developers_phone is not null");
            return (Criteria) this;
        }

        public Criteria andDevelopersPhoneEqualTo(String value) {
            addCriterion("developers_phone =", value, "developersPhone");
            return (Criteria) this;
        }

        public Criteria andDevelopersPhoneNotEqualTo(String value) {
            addCriterion("developers_phone <>", value, "developersPhone");
            return (Criteria) this;
        }

        public Criteria andDevelopersPhoneGreaterThan(String value) {
            addCriterion("developers_phone >", value, "developersPhone");
            return (Criteria) this;
        }

        public Criteria andDevelopersPhoneGreaterThanOrEqualTo(String value) {
            addCriterion("developers_phone >=", value, "developersPhone");
            return (Criteria) this;
        }

        public Criteria andDevelopersPhoneLessThan(String value) {
            addCriterion("developers_phone <", value, "developersPhone");
            return (Criteria) this;
        }

        public Criteria andDevelopersPhoneLessThanOrEqualTo(String value) {
            addCriterion("developers_phone <=", value, "developersPhone");
            return (Criteria) this;
        }

        public Criteria andDevelopersPhoneLike(String value) {
            addCriterion("developers_phone like", value, "developersPhone");
            return (Criteria) this;
        }

        public Criteria andDevelopersPhoneNotLike(String value) {
            addCriterion("developers_phone not like", value, "developersPhone");
            return (Criteria) this;
        }

        public Criteria andDevelopersPhoneIn(List<String> values) {
            addCriterion("developers_phone in", values, "developersPhone");
            return (Criteria) this;
        }

        public Criteria andDevelopersPhoneNotIn(List<String> values) {
            addCriterion("developers_phone not in", values, "developersPhone");
            return (Criteria) this;
        }

        public Criteria andDevelopersPhoneBetween(String value1, String value2) {
            addCriterion("developers_phone between", value1, value2, "developersPhone");
            return (Criteria) this;
        }

        public Criteria andDevelopersPhoneNotBetween(String value1, String value2) {
            addCriterion("developers_phone not between", value1, value2, "developersPhone");
            return (Criteria) this;
        }

        public Criteria andDepIdIsNull() {
            addCriterion("dep_id is null");
            return (Criteria) this;
        }

        public Criteria andDepIdIsNotNull() {
            addCriterion("dep_id is not null");
            return (Criteria) this;
        }

        public Criteria andDepIdEqualTo(Integer value) {
            addCriterion("dep_id =", value, "depId");
            return (Criteria) this;
        }

        public Criteria andDepIdNotEqualTo(Integer value) {
            addCriterion("dep_id <>", value, "depId");
            return (Criteria) this;
        }

        public Criteria andDepIdGreaterThan(Integer value) {
            addCriterion("dep_id >", value, "depId");
            return (Criteria) this;
        }

        public Criteria andDepIdGreaterThanOrEqualTo(Integer value) {
            addCriterion("dep_id >=", value, "depId");
            return (Criteria) this;
        }

        public Criteria andDepIdLessThan(Integer value) {
            addCriterion("dep_id <", value, "depId");
            return (Criteria) this;
        }

        public Criteria andDepIdLessThanOrEqualTo(Integer value) {
            addCriterion("dep_id <=", value, "depId");
            return (Criteria) this;
        }

        public Criteria andDepIdIn(List<Integer> values) {
            addCriterion("dep_id in", values, "depId");
            return (Criteria) this;
        }

        public Criteria andDepIdNotIn(List<Integer> values) {
            addCriterion("dep_id not in", values, "depId");
            return (Criteria) this;
        }

        public Criteria andDepIdBetween(Integer value1, Integer value2) {
            addCriterion("dep_id between", value1, value2, "depId");
            return (Criteria) this;
        }

        public Criteria andDepIdNotBetween(Integer value1, Integer value2) {
            addCriterion("dep_id not between", value1, value2, "depId");
            return (Criteria) this;
        }

        public Criteria andManualDepIdIsNull() {
            addCriterion("manual_dep_id is null");
            return (Criteria) this;
        }

        public Criteria andManualDepIdIsNotNull() {
            addCriterion("manual_dep_id is not null");
            return (Criteria) this;
        }

        public Criteria andManualDepIdEqualTo(Integer value) {
            addCriterion("manual_dep_id =", value, "manualDepId");
            return (Criteria) this;
        }

        public Criteria andManualDepIdNotEqualTo(Integer value) {
            addCriterion("manual_dep_id <>", value, "manualDepId");
            return (Criteria) this;
        }

        public Criteria andManualDepIdGreaterThan(Integer value) {
            addCriterion("manual_dep_id >", value, "manualDepId");
            return (Criteria) this;
        }

        public Criteria andManualDepIdGreaterThanOrEqualTo(Integer value) {
            addCriterion("manual_dep_id >=", value, "manualDepId");
            return (Criteria) this;
        }

        public Criteria andManualDepIdLessThan(Integer value) {
            addCriterion("manual_dep_id <", value, "manualDepId");
            return (Criteria) this;
        }

        public Criteria andManualDepIdLessThanOrEqualTo(Integer value) {
            addCriterion("manual_dep_id <=", value, "manualDepId");
            return (Criteria) this;
        }

        public Criteria andManualDepIdIn(List<Integer> values) {
            addCriterion("manual_dep_id in", values, "manualDepId");
            return (Criteria) this;
        }

        public Criteria andManualDepIdNotIn(List<Integer> values) {
            addCriterion("manual_dep_id not in", values, "manualDepId");
            return (Criteria) this;
        }

        public Criteria andManualDepIdBetween(Integer value1, Integer value2) {
            addCriterion("manual_dep_id between", value1, value2, "manualDepId");
            return (Criteria) this;
        }

        public Criteria andManualDepIdNotBetween(Integer value1, Integer value2) {
            addCriterion("manual_dep_id not between", value1, value2, "manualDepId");
            return (Criteria) this;
        }

        public Criteria andDepPlatModifyTimeIsNull() {
            addCriterion("dep_plat_modify_time is null");
            return (Criteria) this;
        }

        public Criteria andDepPlatModifyTimeIsNotNull() {
            addCriterion("dep_plat_modify_time is not null");
            return (Criteria) this;
        }

        public Criteria andDepPlatModifyTimeEqualTo(Date value) {
            addCriterion("dep_plat_modify_time =", value, "depPlatModifyTime");
            return (Criteria) this;
        }

        public Criteria andDepPlatModifyTimeNotEqualTo(Date value) {
            addCriterion("dep_plat_modify_time <>", value, "depPlatModifyTime");
            return (Criteria) this;
        }

        public Criteria andDepPlatModifyTimeGreaterThan(Date value) {
            addCriterion("dep_plat_modify_time >", value, "depPlatModifyTime");
            return (Criteria) this;
        }

        public Criteria andDepPlatModifyTimeGreaterThanOrEqualTo(Date value) {
            addCriterion("dep_plat_modify_time >=", value, "depPlatModifyTime");
            return (Criteria) this;
        }

        public Criteria andDepPlatModifyTimeLessThan(Date value) {
            addCriterion("dep_plat_modify_time <", value, "depPlatModifyTime");
            return (Criteria) this;
        }

        public Criteria andDepPlatModifyTimeLessThanOrEqualTo(Date value) {
            addCriterion("dep_plat_modify_time <=", value, "depPlatModifyTime");
            return (Criteria) this;
        }

        public Criteria andDepPlatModifyTimeIn(List<Date> values) {
            addCriterion("dep_plat_modify_time in", values, "depPlatModifyTime");
            return (Criteria) this;
        }

        public Criteria andDepPlatModifyTimeNotIn(List<Date> values) {
            addCriterion("dep_plat_modify_time not in", values, "depPlatModifyTime");
            return (Criteria) this;
        }

        public Criteria andDepPlatModifyTimeBetween(Date value1, Date value2) {
            addCriterion("dep_plat_modify_time between", value1, value2, "depPlatModifyTime");
            return (Criteria) this;
        }

        public Criteria andDepPlatModifyTimeNotBetween(Date value1, Date value2) {
            addCriterion("dep_plat_modify_time not between", value1, value2, "depPlatModifyTime");
            return (Criteria) this;
        }

        public Criteria andCreateTimeIsNull() {
            addCriterion("create_time is null");
            return (Criteria) this;
        }

        public Criteria andCreateTimeIsNotNull() {
            addCriterion("create_time is not null");
            return (Criteria) this;
        }

        public Criteria andCreateTimeEqualTo(Date value) {
            addCriterion("create_time =", value, "createTime");
            return (Criteria) this;
        }

        public Criteria andCreateTimeNotEqualTo(Date value) {
            addCriterion("create_time <>", value, "createTime");
            return (Criteria) this;
        }

        public Criteria andCreateTimeGreaterThan(Date value) {
            addCriterion("create_time >", value, "createTime");
            return (Criteria) this;
        }

        public Criteria andCreateTimeGreaterThanOrEqualTo(Date value) {
            addCriterion("create_time >=", value, "createTime");
            return (Criteria) this;
        }

        public Criteria andCreateTimeLessThan(Date value) {
            addCriterion("create_time <", value, "createTime");
            return (Criteria) this;
        }

        public Criteria andCreateTimeLessThanOrEqualTo(Date value) {
            addCriterion("create_time <=", value, "createTime");
            return (Criteria) this;
        }

        public Criteria andCreateTimeIn(List<Date> values) {
            addCriterion("create_time in", values, "createTime");
            return (Criteria) this;
        }

        public Criteria andCreateTimeNotIn(List<Date> values) {
            addCriterion("create_time not in", values, "createTime");
            return (Criteria) this;
        }

        public Criteria andCreateTimeBetween(Date value1, Date value2) {
            addCriterion("create_time between", value1, value2, "createTime");
            return (Criteria) this;
        }

        public Criteria andCreateTimeNotBetween(Date value1, Date value2) {
            addCriterion("create_time not between", value1, value2, "createTime");
            return (Criteria) this;
        }

        public Criteria andModifyTimeIsNull() {
            addCriterion("modify_time is null");
            return (Criteria) this;
        }

        public Criteria andModifyTimeIsNotNull() {
            addCriterion("modify_time is not null");
            return (Criteria) this;
        }

        public Criteria andModifyTimeEqualTo(Date value) {
            addCriterion("modify_time =", value, "modifyTime");
            return (Criteria) this;
        }

        public Criteria andModifyTimeNotEqualTo(Date value) {
            addCriterion("modify_time <>", value, "modifyTime");
            return (Criteria) this;
        }

        public Criteria andModifyTimeGreaterThan(Date value) {
            addCriterion("modify_time >", value, "modifyTime");
            return (Criteria) this;
        }

        public Criteria andModifyTimeGreaterThanOrEqualTo(Date value) {
            addCriterion("modify_time >=", value, "modifyTime");
            return (Criteria) this;
        }

        public Criteria andModifyTimeLessThan(Date value) {
            addCriterion("modify_time <", value, "modifyTime");
            return (Criteria) this;
        }

        public Criteria andModifyTimeLessThanOrEqualTo(Date value) {
            addCriterion("modify_time <=", value, "modifyTime");
            return (Criteria) this;
        }

        public Criteria andModifyTimeIn(List<Date> values) {
            addCriterion("modify_time in", values, "modifyTime");
            return (Criteria) this;
        }

        public Criteria andModifyTimeNotIn(List<Date> values) {
            addCriterion("modify_time not in", values, "modifyTime");
            return (Criteria) this;
        }

        public Criteria andModifyTimeBetween(Date value1, Date value2) {
            addCriterion("modify_time between", value1, value2, "modifyTime");
            return (Criteria) this;
        }

        public Criteria andModifyTimeNotBetween(Date value1, Date value2) {
            addCriterion("modify_time not between", value1, value2, "modifyTime");
            return (Criteria) this;
        }
    }

    public static class Criteria extends GeneratedCriteria {
        /**
         * This field was generated by MyBatis Generator.
         * This field corresponds to the database table app_dep
         *
         * @mbg.generated
         * @project https://github.com/itfsw/mybatis-generator-plugin
         */
        private AppDepExample example;

        /**
         * This method was generated by MyBatis Generator.
         * This method corresponds to the database table app_dep
         *
         * @mbg.generated
         * @project https://github.com/itfsw/mybatis-generator-plugin
         */
        protected Criteria(AppDepExample example) {
            super();
            this.example = example;
        }

        /**
         * This method was generated by MyBatis Generator.
         * This method corresponds to the database table app_dep
         *
         * @mbg.generated
         * @project https://github.com/itfsw/mybatis-generator-plugin
         */
        public AppDepExample example() {
            return this.example;
        }

        /**
         * This method was generated by MyBatis Generator.
         * This method corresponds to the database table app_dep
         *
         * @mbg.generated
         * @project https://github.com/itfsw/mybatis-generator-plugin
         */
        public Criteria andIf(boolean ifAdd, ICriteriaAdd add) {
            if (ifAdd) {
                add.add(this);
            }
            return this;
        }

        /**
         * This interface was generated by MyBatis Generator.
         * This interface corresponds to the database table app_dep
         *
         * @mbg.generated
         * @project https://github.com/itfsw/mybatis-generator-plugin
         */
        public interface ICriteriaAdd {
            /**
             * This method was generated by MyBatis Generator.
             * This method corresponds to the database table app_dep
             *
             * @mbg.generated
             * @project https://github.com/itfsw/mybatis-generator-plugin
             */
            Criteria add(Criteria add);
        }
    }

    public static class Criterion {
        private String condition;

        private Object value;

        private Object secondValue;

        private boolean noValue;

        private boolean singleValue;

        private boolean betweenValue;

        private boolean listValue;

        private String typeHandler;

        public String getCondition() {
            return condition;
        }

        public Object getValue() {
            return value;
        }

        public Object getSecondValue() {
            return secondValue;
        }

        public boolean isNoValue() {
            return noValue;
        }

        public boolean isSingleValue() {
            return singleValue;
        }

        public boolean isBetweenValue() {
            return betweenValue;
        }

        public boolean isListValue() {
            return listValue;
        }

        public String getTypeHandler() {
            return typeHandler;
        }

        protected Criterion(String condition) {
            super();
            this.condition = condition;
            this.typeHandler = null;
            this.noValue = true;
        }

        protected Criterion(String condition, Object value, String typeHandler) {
            super();
            this.condition = condition;
            this.value = value;
            this.typeHandler = typeHandler;
            if (value instanceof List<?>) {
                this.listValue = true;
            } else {
                this.singleValue = true;
            }
        }

        protected Criterion(String condition, Object value) {
            this(condition, value, null);
        }

        protected Criterion(String condition, Object value, Object secondValue, String typeHandler) {
            super();
            this.condition = condition;
            this.value = value;
            this.secondValue = secondValue;
            this.typeHandler = typeHandler;
            this.betweenValue = true;
        }

        protected Criterion(String condition, Object value, Object secondValue) {
            this(condition, value, secondValue, null);
        }
    }
}