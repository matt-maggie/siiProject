package org.sii;

import java.util.List;

import org.jinstagram.Instagram;
import org.jinstagram.entity.users.feed.UserFeed;
import org.jinstagram.entity.users.feed.UserFeedData;
import org.jinstagram.exceptions.InstagramException;

public class RetrieveFollowers {
	public String retrieveFollowers(String id,Instagram instagram) throws InstagramException {
		String followers="";
		UserFeed uFeed =instagram.getUserFollowList(id);
		List<UserFeedData> udata = uFeed.getUserList();
		for(UserFeedData d: udata){
			followers = followers + d.getId()+",";
		}

		return followers;

	}




}
