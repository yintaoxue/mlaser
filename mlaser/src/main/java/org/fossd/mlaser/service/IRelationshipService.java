package org.fossd.mlaser.service;

import java.util.List;

public interface IRelationshipService {
	public boolean follow(String userId, String toFollow);
	public boolean unfollow(String userId, String toUnFollow);
	public List<String> getFollwings(String userId);
	public List<String> getFollwers(String userId);
}
