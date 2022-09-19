package nju.merge.entity;

/**
 * 分析 token conflict 解决标签的枚举类
 */
public enum ResolutionLabel {
    ACCEPT_A,
    ACCEPT_B,
    ACCEPT_BASE,
    CONCAT_AB,
    CONCAT_BA,
    NONE
}
