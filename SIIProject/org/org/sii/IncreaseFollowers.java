package org.sii;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.jinstagram.entity.common.Pagination;
import org.jinstagram.entity.common.User;
import org.jinstagram.entity.likes.LikesFeed;
import org.jinstagram.entity.relationships.RelationshipData;
import org.jinstagram.entity.relationships.RelationshipFeed;
import org.jinstagram.entity.tags.TagMediaFeed;
import org.jinstagram.entity.users.basicinfo.Counts;
import org.jinstagram.entity.users.basicinfo.UserInfo;
import org.jinstagram.entity.users.basicinfo.UserInfoData;
import org.jinstagram.entity.users.feed.MediaFeed;
import org.jinstagram.entity.users.feed.MediaFeedData;
import org.jinstagram.entity.users.feed.UserFeed;
import org.jinstagram.entity.users.feed.UserFeedData;
import org.jinstagram.exceptions.InstagramException;
import org.jinstagram.model.Relationship;
import org.jinstagram.Instagram;


public class IncreaseFollowers {

	public IncreaseFollowers() {
		super();
		// TODO Auto-generated constructor stub
	}
	List<String> userReached = new ArrayList<String>();
	Map<String,Integer> userIteration = new HashMap<String,Integer>();

	/* Metodo per selezionare gli utenti con rate (followers/following) <=1 e numero di followers <600*/
	public boolean analyze(int followers, int following){
		boolean analyze = false;
		if ( following!=0 && followers!=0 && followers<600 &&(followers/following)<=1 )
			analyze = true;
		return analyze;
	}
	/* Metodo per selezionare gli utenti con rate (followers/following) <=1 e numero di followers >600*/
	public boolean analyzeReversed(int followers, int following){
		boolean analyze = false;
		if ( following!=0 && followers!=0 && followers>600 &&(followers/following)<=1 )
			analyze = true;
		return analyze;
	}

/* metodo di supporto che ritorna il numero di followers dell'utente, dato il suo id*/
	public int followers(String id, Instagram instagram) throws InstagramException{
		UserInfo userInfo = instagram.getUserInfo(id);
		UserInfoData userInfoData = userInfo.getData();
		Counts userCounts = userInfoData.getCounts();
		int followers = userCounts.getFollwed_by();
		return followers;
	}
	/* metodo di supporto che ritorna il numero di followings dell'utente, dato il suo id*/

	public int following(String id, Instagram instagram) throws InstagramException{
		UserInfo userInfo = instagram.getUserInfo(id);
		UserInfoData userInfoData = userInfo.getData();
		Counts userCounts = userInfoData.getCounts();
		int following = userCounts.getFollows();
		return following;
	}
	/* metodo che calcola quanti media ha pubblicato l'utente*/
	public int userCounts(String id,Instagram instagram) throws InstagramException{
		UserInfo userInfo = instagram.getUserInfo(id);
		UserInfoData userInfoData = userInfo.getData();
		Counts userCounts = userInfoData.getCounts();
		int media =userCounts.getMedia();
		return media;
	}
	/* tag da ricercare per l'approccio con db*/
	public boolean isTags(MediaFeedData mediaFeedData, Instagram instagram)throws InstagramException{
		boolean isTag = false;
		for (String s : mediaFeedData.getTags()){
			if (s.equals("foodporn")||s.equals("food")||s.equals("yummi") ||s.equals("instafood")||s.equals("cooking"))
				isTag=true;}

		return isTag;

	}
/* Metodo per inviare i likes(in numero uguale a quello del contatore, a utenti ricercati tramite un tag*/
	public  void increaseFollowersLike(String tagName, long cont, Instagram instagram) throws InstagramException {
		TagMediaFeed tagMediaFeed = instagram.getRecentMediaTags(tagName, cont);
		List<MediaFeedData> data = tagMediaFeed.getData();
		Pagination pagination;
		int contatore = 30;
		int contaTag =0;
		while(contatore >1){
			if(contaTag>=32){
				pagination = tagMediaFeed.getPagination();
				tagMediaFeed =instagram.getTagMediaInfoNextPage(pagination);
				data= tagMediaFeed.getData();
				System.out.println("next lap!!");
			}
			for (MediaFeedData m : data){
				contaTag++;
				User u = m.getUser();
				String userId = u.getId();
				if (analyze(followers(userId,instagram), following(userId,instagram)) && contatore >0){
					if (userCounts(userId,instagram)>= 3){
						contatore --;
						MediaFeed media = instagram.getRecentMediaFeed(userId, 3, null, null, null, null);
						userReached.add(userId);
						List<MediaFeedData> elements = media.getData();
						for (MediaFeedData mediaFeedData : elements){
							if (!mediaFeedData.isUserHasLiked()){
								String idData = mediaFeedData.getId();
								LikesFeed like = instagram.setUserLike(idData); //mette il like all'utente
								System.out.println("like");
								try {
									Thread.sleep(1000);
								} catch (InterruptedException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							}
						}
					}

				}
			}
		}
		//return userReached;
	} //end IncreaseFollowersLike
/*Metodo per incrementare il numero di followers inviando un follow agli utenti scelti in base a uno specifico tag*/
	public  void increaseFollowersFollow(String tagName, long cont, Instagram instagram) throws InstagramException {
		TagMediaFeed tagMediaFeed = instagram.getRecentMediaTags(tagName, cont);
		List<MediaFeedData> data = tagMediaFeed.getData();
		int contatore = 58;
		int contaTag =0;
		Relationship relationship;
		relationship=Relationship.FOLLOW;
		Pagination pagination;
		while(contatore >1){
			if(contaTag>=32){
				pagination = tagMediaFeed.getPagination();
				tagMediaFeed =instagram.getTagMediaInfoNextPage(pagination);
				data= tagMediaFeed.getData();
				System.out.println("prossimo giro!!");

			}
			for (MediaFeedData m : data){
				contaTag++;
				System.out.println("****ContaTag****"+ contaTag);
				User u = m.getUser();
				String userId = u.getId();
				if (!userReached.contains(userId)){
					if (analyze(followers(userId,instagram), following(userId,instagram)) && contatore>1){
						userReached.add(userId);
						System.out.println("contatore"+ contatore);
						contatore--;
						instagram.setUserRelationship(userId,relationship);
						
						try {
							Thread.sleep(1000);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}


					}	//end if analyze
				} //end if lista

			} }//end for
	} //end IncreaseFollowersFollow

/*Metodo per aumentare il numero di followers, prendendo gli elementi salvati all'interno del database(utenti che hanno pubblicato
 *  foto con i tags di isTags recentemente*/
	public void increaseFollowerByDB(Instagram instagram) throws Exception{
		DBManager c = new DBManager();
		Relationship relationship;
		relationship=Relationship.FOLLOW; //crea Relationship
		Map<String,Integer> map = c.userExtractor(); // questo metodo estrae gli utenti dal DB con numero di foto di isTags >n
		c.sortMap(map); //ordino la mappa dei risultati
		int contatore =58;
		Iterator it = map.entrySet().iterator();
		while (it.hasNext()&& contatore >0) {
			// Utilizza il nuovo elemento (coppia chiave-valore)
			// dell'hashmap
			Map.Entry entry = (Map.Entry)it.next();
			String userId = (String) entry.getKey();
			instagram.setUserRelationship(userId,relationship);
			System.out.println("followato");
			c.deleteUser(userId);
		}
	}
/* cerca gli utenti tramite un tag e li memorizza nel DB*/
	public  void searchFollowersByNumber(String tagName, long cont, Instagram instagram) throws Exception {
		TagMediaFeed tagMediaFeed = instagram.getRecentMediaTags(tagName, cont);
		DBManager createConnection = new DBManager();
		List<MediaFeedData> data = tagMediaFeed.getData();
		int contatore = 4990;//il limite delle api è di 5000 richieste all'ora
		int contaTag =0;
		Pagination pagination;
		while(contatore >1){
			System.out.println("contatore+"+contatore);
			if(contaTag>=32){
				pagination = tagMediaFeed.getPagination();
				tagMediaFeed =instagram.getTagMediaInfoNextPage(pagination);
				data= tagMediaFeed.getData();
				System.out.println("prossimo giro!!");

			}
			for (MediaFeedData m : data){
				contaTag++;
				String userId = m.getUser().getId();
				if (analyzeReversed(followers(userId,instagram), following(userId,instagram))&& contatore>1){
					if (userCounts(userId,instagram) >= 50){ //deve avere almeno 50 foto?
						System.out.println("preso");
						int number=0;
						MediaFeed media = instagram.getRecentMediaFeed(userId, 50, null, null, null, null);
						List<MediaFeedData> elements = media.getData();
						for (MediaFeedData mediaFeedData : elements){
							contatore--;
							if(isTags(mediaFeedData,instagram))//se c'è il tag foodporn food etc
								number++;
						}
						//inserisci utente nel DB con il numero di foto in cui compare il tag
						createConnection.insertUser(userId,number);
						createConnection.insertUserTab2(userId, number);
						System.out.println("inserito");
					}
				}	//end if analyze


			} //end for
		}//end while
	} //end metodo



} //end class
