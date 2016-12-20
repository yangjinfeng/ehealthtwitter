package ehealth.website;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import ehealth.tweet.ActorVO;

public class CommunityMainPage {

	static String[] networks = new String[] { "total_network", "2016-03-23-network", "2016-03-24-network",
			"2016-03-25-network", "2016-03-26-network", "2016-03-27-network", "2016-03-28-network",
			"2016-03-29-network", "2016-03-30-network", "2016-03-31-network", };

	private HashMap<String, ActorVO> actMap = new HashMap<String, ActorVO>();

	public static void main(String[] args) throws Exception {
		String srcDir = args[0];
		int topK = Integer.parseInt(args[1]);
		int topN = Integer.parseInt(args[2]);
		String outputDir = args[3];
		CommunityMainPage cm = new CommunityMainPage();
		for (String nw : networks) {
			cm.createMainTable(srcDir, nw, topK, topN, outputDir);
		}
	}

	private void loadActors(String srcDir) throws Exception {
		BufferedReader br = new BufferedReader(
				new InputStreamReader(new FileInputStream(srcDir + "/kloutScore_iDname.txt"), "UTF-8"));
		String line = null;
		while ((line = br.readLine()) != null) {
			String[] fs = line.split(",");
			ActorVO a = new ActorVO();
			a.setActorId(fs[0]);
			a.setActorName(fs[2]);
			a.setKloutScore(Integer.parseInt(fs[3]));
			a.setVerified(fs[1]);
			actMap.put(a.getActorId(), a);
		}
		br.close();
	}

	private String actorToHtml(ActorVO av, int outDegree,int indegree) {
		String pageurl = "http://www.twitter.com/" + av.getActorName();
		String display = av.getActorName();
		String info = "out:" + outDegree+",in:"+indegree;
		if (av.getKloutScore() > 0) {
			info = info + ", klout:" + av.getKloutScore();
		}
		display = display + "(" + info + ")";
		if (av.getVerified().equals("True")) {
			display = "<span style=\"color:green;font-weight:bold;\">" + display + "</span>";
		}
		String href = "<a href=\"" + pageurl + "\" target=\"_blank\">" + display + "</a>";
		return href;
	}

	private static String listToTable(List<String> list) {
		StringBuffer sb = new StringBuffer();
		sb.append("<table>");
		for (int i = 0; i < list.size(); i++) {
			if (i == 0) {
				sb.append("<tr>");
			}
			if (i > 0 && i % 3 == 0) {
				sb.append("</tr><tr>");
			}
			if (i <= list.size() - 1) {
				sb.append("<td>" + list.get(i) + "</td>");
			}
			if (i == list.size() - 1) {
				sb.append("</tr>");
			}

		}
		sb.append("</table>");
		return sb.toString();
	}

	public void createMainTable(String srcDir, String network, int topK, int topN, String outputDir) throws Exception {
		PrintWriter pw = new PrintWriter(outputDir + "/" + network + ".html");
		String datename = network.replaceAll("[-_]network", "");
		pw.println("<html><head>");
		pw.println("<script type=\"text/javascript\" src=\"/js/tool.js\"></script>");
		pw.println(datename + " &nbsp; top " + topN + " communities are listed  <br>");
		pw.println("<table border=\"1\">");
		pw.println("<tr><td width=\"2%\">id</td><td width=\"45%\">word cloud of topic</td><td width=\"45%\">top " + topK
				+ " users of each community</td><td width=\"8%\">topic(json)</td></tr>");

		loadActors(srcDir);
		String communityFile = srcDir + "/SLPAw_" + network + "_weighted_run1_r0.25_v3_T100.icpm_ordered";

		this.createCommunityUserHtml(srcDir, network, outputDir, communityFile);

		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(communityFile), "UTF-8"));
		String line = null;
		int commid = 0;
		while ((line = br.readLine()) != null) {
			commid++;

			// 生成词云图片列
			String picname = "community_" + commid + "_topic.png";
			String hashtag = "/topic_visual/hashtags/" + datename + "/community_" + commid + "_hashtags.png";
			String with_verified = "/topic_visual/" + datename + "/with-verified-users/" + picname;
			String without_verified = "/topic_visual/" + datename + "/without-verified-users/" + picname;

			String wordcloudHtml = "with verified &nbsp;&nbsp;&nbsp;<input type=\"button\" value=\"hashtag_topic\" onClick=\"openDialog4Topic('"
					+ hashtag + "',200,0)\"><br>";
			wordcloudHtml = wordcloudHtml + "<img src=\"" + with_verified + "\" style=\"width:500px;\"> <br>";
			wordcloudHtml = wordcloudHtml + "without verified<br>";
			wordcloudHtml = wordcloudHtml + "<img src=\"" + without_verified + "\" style=\"width:500px;\"> <br>";

			// 生成社区的用户列
			String[] fs = line.split(" ");
			int k = 0;
			List<String> userHtmls = new ArrayList<String>();
			for (String actid : fs) {
				k++;
				ActorVO av = actMap.get(actid.substring(0, actid.indexOf('#')));
				int outdegree = Integer.parseInt(actid.substring(actid.indexOf('#') + 1,actid.lastIndexOf('#')));
				int indegree = Integer.parseInt(actid.substring(actid.lastIndexOf('#') + 1));
				String html = actorToHtml(av, outdegree,indegree);
				userHtmls.add(html);
				if (k >= topK) {
					break;
				}

			}
			String commHtml = listToTable(userHtmls);

			// 生成topic的json列
			String topicjson = "";
			for (String type : new String[] { "with-verified-users", "without-verified-users" }) {
				topicjson = "<br>" + topicjson + type + "<br>";
				String[] langs = new String[] { "en", "ar" };
				for (String lang : langs) {
					String filename = "topic/" + datename + "/" + type + "/community-" + commid + "-tweets-" + lang
							+ ".json";
					if (new File(filename).exists()) {
						topicjson = topicjson + "<input type=\"button\" value=\"" + lang
								+ "\" onClick=\"openDialog4Topic('/" + filename + "',-700,-200)\"><br>";
					}
				}
			}

			// 显示hashtag
			String hashtagjson = "topic/hashtag/" + datename + "-community-" + commid + "-hashtag.txt";
			if (new File(hashtagjson).exists()) {
				topicjson = topicjson + "<br><input type=\"button\" value=\"hashtag\" onClick=\"openDialog4Hashtag('/"
						+ hashtagjson + "',-700,-100)\"><br>";
			}

			String tweetjson = "";
			if (new File("tweets/" + datename + "/community-" + commid + "-tweets-en").exists()) {
				tweetjson = "<br><input type=\"button\" value=\"tweets-en\" onClick=\"openDialog4Tweet('" + "/tweets/"
						+ datename + "/community-" + commid + "-tweets-en" + "',-1100,-100)\"><br>";
			}

			if (new File("tweets/" + datename + "/community-" + commid + "-tweets-ar").exists()) {
				tweetjson = tweetjson + "<br><input type=\"button\" value=\"tweets-ar\" onClick=\"openDialog4Tweet('"
						+ "/tweets/" + datename + "/community-" + commid + "-tweets-ar" + "',-1100,-100)\"><br>";
			}
			
			String degreepic="";
			if (new File("degree/"+datename+"/"+commid+".jpg").exists()) {
				degreepic = "<br><input type=\"button\" value=\"degree(in/out)\" onClick=\"openDialog4Degree('"
						+ "/degree/"+datename+"/"+commid+".jpg" + "',-500,-100)\"><br>";
			}

			String tr = "<tr><td>" + commid + "</td><td>" + wordcloudHtml + "</td><td>" + commHtml + "</td>" + "<td>"
					+ degreepic+"<br>" +topicjson + "<br>" + "<a href=\"./" + network + "/" + commid
					+ ".html\" target=\"_blank\">more users</a><br>" + tweetjson + "</td></tr>";

			pw.println(tr);
			if (commid >= topN) {
				break;
			}
		}
		pw.println("</table>");
		pw.flush();
		pw.close();

		br.close();
	}

	private void createCommunityUserHtml(String srcDir, String network, String outputDir, String communityFile)
			throws Exception {
		String outputfolder = outputDir + "/" + network + "/";
		File folder = new File(outputfolder);
		if (!folder.exists()) {
			folder.mkdir();
		}

		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(communityFile), "UTF-8"));
		String line = null;
		int commid = 0;
		while ((line = br.readLine()) != null) {
			commid++;
			PrintWriter pw = new PrintWriter(outputDir + "/" + network + "/" + commid + ".html");
			pw.println("All users in community #" + commid + "<br>");

			String[] fs = line.split(" ");

			List<String> userHtmls = new ArrayList<String>();
			for (String actid : fs) {
				ActorVO av = actMap.get(actid.substring(0, actid.indexOf('#')));
				int outdegree = Integer.parseInt(actid.substring(actid.indexOf('#') + 1,actid.lastIndexOf('#')));
				int indegree = Integer.parseInt(actid.substring(actid.lastIndexOf('#') + 1));
				String html = actorToHtml(av, outdegree,indegree);
				userHtmls.add(html);
			}

			// 输出html内容到文件
			int userSize = userHtmls.size();
			StringBuffer sb = new StringBuffer();
			sb.append("<table border=\"1\">");
			for (int i = 0; i < userSize; i++) {
				if (i % 7 == 0) {
					sb.append("<tr>");
				}
				sb.append("<td>" + userHtmls.get(i) + "</td>");
				if (i % 7 == 6) {
					sb.append("</tr>");
				}

			}
			sb.append("</table>");
			pw.println(sb.toString());

			pw.flush();
			pw.close();
		}
		br.close();
	}

}
