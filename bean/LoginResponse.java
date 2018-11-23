package com.fousalert.bean;

import java.util.ArrayList;
import java.util.List;

import com.fousalert.database.beans.ResultBean;

public class LoginResponse extends ResultBean {
	
	private Long userId;
	private UIUserGroupEntitlement userGroup;
	private List<UIUserGroupEntitlement> entitlements = new ArrayList<UIUserGroupEntitlement>();
	
	public Long getUserId() {
		return userId;
	}
	public void setUserId(Long userId) {
		this.userId = userId;
	}
	public List<UIUserGroupEntitlement> getEntitlements() {
		return entitlements;
	}
	public void setEntitlements(List<UIUserGroupEntitlement> entitlements) {
		this.entitlements = entitlements;
	}
	public UIUserGroupEntitlement getUserGroup() {
		return userGroup;
	}
	public void setUserGroup(UIUserGroupEntitlement userGroup) {
		this.userGroup = userGroup;
	}
}
