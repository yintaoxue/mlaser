package org.fossd.mlaser.service;

import java.util.List;

import org.fossd.mlaser.domain.Feed;

public interface IFeedService {
	public Feed getFeedById(String feedId);
	public void addFeed(Feed feed);
	public void removeFeed(String feedId);
	public List<Feed> getHomeTimeline(String userId);
	public List<Feed> getSelfTimeLine(String userId);
}
