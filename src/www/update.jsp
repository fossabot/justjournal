<%@ page contentType="text/html; charset=iso-8859-1" language="java" import="com.justjournal.db.*" %>
<%
    // date stuff
    java.text.SimpleDateFormat fmt = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    java.sql.Date now = new java.sql.Date(System.currentTimeMillis());

// Get the session user input
    String sbody = (String) session.getAttribute("spell.body");
    String smusic = (String) session.getAttribute("spell.music");
    String ssubject = (String) session.getAttribute("spell.subject");

    if (sbody == null)
        sbody = "";

    if (smusic == null)
        smusic = "";

    if (ssubject == null)
        ssubject = "";

%>
<?xml version="1.0" encoding="iso-8859-1"?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">

<html xmlns="http://www.w3.org/1999/xhtml">

<head>
    <meta content="text/html; charset=ISO-8859-1" http-equiv="Content-Type"/>
    <title>JustJournal.com: Update Journal</title>
    <link rel="stylesheet" type="text/css" href="layout.css" media="all"/>
    <link rel="stylesheet" type="text/css" href="font-normal.css" media="all"/>
    <link rel="home" title="Home" href="index.jsp"/>
    <link rel="contents" title="Site Map" href="sitemap.jsp"/>
    <link rel="help" title="Technical Support" href="support/index.jsp"/>
    <script type="text/javascript" src="FCKeditor/fckeditor.js">//iesucks</script>
    <script type="text/javascript">
        window.onload = function()
        {
            var oFCKeditor = new FCKeditor('body');
            oFCKeditor.BasePath = '/FCKeditor/';
            oFCKeditor.Height = 300;
            oFCKeditor.Width = 400;
            oFCKeditor.Config['SkinPath'] = '/FCKeditor/editor/skins/office2003/';
            oFCKeditor.Config['AutoDetectLanguage'] = true;
            oFCKeditor.EnableSafari = false;
            oFCKeditor.ToolbarSet = 'Basic';
            oFCKeditor.ReplaceTextarea();
        }

        function FCKeditor_OnComplete(editorInstance)
        {
            document.getElementById("aformat").checked = false;
        }
    </script>
    <style type="text/css" media="all">
        <!--

        div.row {
            clear: both;
            padding-top: 5px;
        }

        div.row span.label {
            float: left;
            width: 140px;
            text-align: right;
        }

        div.row span.formw {
            float: right;
            width: 400px;
            text-align: left;
        }

        div.spacer {
            clear: both;
        }

        -->
    </style>
</head>

<body>

<jsp:include page="header.inc" flush="false"/>

<div id="content">
<h2>Update Journal</h2>

<p>Post a journal entry.</p>

<div style="width: 575px; padding: 5px; margin: 0;">
<form method="post" action="updateJournal" name="frmUpdateJournal">

<%
    Integer userID = (Integer) session.getAttribute("auth.uid");
    int ival = 0;
    if (userID != null) {
        ival = userID.intValue();
    }

    if (ival < 1) {
%>
<fieldset>
    <legend><strong>Login Information</strong><br/></legend>

    <div class="row">
        <span class="label"><label for="user">Username</label></span>
			<span class="formw"><input type="text" name="user" id="user" size="18" maxlength="15"
                                       style="background: url(images/userclass_16.png) no-repeat; background-color: #fff; background-position: 0px 1px; padding-left: 18px; color: black; font-weight: bold;"/></span>
    </div>

    <div class="row">
        <span class="label"><label for="pass">Password</label></span>
			<span class="formw"><input type="password" name="pass" id="pass" size="18" maxlength="18"
                                       style="background: white; color: black; font-weight: bold;"/>
			</span>
    </div>

    <div class="row">
			<span class="formw"><input type="checkbox" name="keeplogin" id="keeplogin"
                                       value="checked" checked="checked"/>
			<label for="keeplogin">Keep login after post</label>
			</span>
    </div>

    <!-- Hack to fix spacing problem.. especially with text boxes -->
    <div class="spacer">&nbsp;</div>

</fieldset>
<% } else { %>
<p>You are logged in as <a href="/users/<%= session.getAttribute("auth.user") %>"><img src="images/userclass_16.png"
                                                                                       alt="user"/><%= session.getAttribute("auth.user") %>
</a>.
    If you want to post to another journal, <a href="logout.jsp">log out</a> first.</p>
<% } %>

<fieldset>
    <legend><strong>Journal Entry</strong><br/></legend>

    <div class="row">
        <span class="label"><label for="date">Date</label></span>
	  <span class="formw"><input name="date" id="date" value="<%=fmt.format( now ) %>" size="25" maxlength="19"/>
	  </span>
    </div>

    <div class="row">
        <span class="label"><label for="subject">Subject</label></span>
	  <span class="formw"><input name="subject" type="text" id="subject" size="25" maxlength="150"
                                 value="<%=ssubject%>"/>
	  (optional)</span>
    </div>
    <%
        String spellcheck = (String) session.getAttribute("spell.check");

        if (spellcheck == null)
            spellcheck = "";

        if (spellcheck.compareTo("true") == 0) {
    %>

    <div class="row">
        <strong>Your spell-checked post:</strong><br/>
        <%=sbody%>
    </div>

    <div class="row">
        <strong>Suggestions:</strong><br/>
        <%=(String) session.getAttribute("spell.suggest")%>
    </div>
    <%
        }
    %>

    <div class="row">
        <span class="label"><label for="body">Body</label></span>
        <span class="formw"><textarea id="body" name="body" style="width: 100%" cols="50"
                                      rows="20"><%=sbody%></textarea></span>
    </div>

    <div class="row">
        <span class="formw">by default, newlines will be auto-formatted to &lt;br&gt;</span>
    </div>

    <div class="row">
			<span class="formw"><input type="checkbox" name="spellcheck" id="spellcheck"
                                       value="checked"/>
			<label for="spellcheck">Spell check entry before posting</label>
			</span>
    </div>

    <!-- Hack to fix spacing problem.. especially with text boxes -->
    <div class="spacer">&nbsp;</div>

</fieldset>

<fieldset>
<legend><strong>Optional Settings</strong><br/></legend>

<div class="row">
    <span class="label"><label for="security">Security</label></span>
	  <span class="formw">
	  	<select id="security" name="security" size="1">
              <%
                  Integer ssec = (Integer) session.getAttribute("spell.security");
                  int issec = 0;
                  if (ssec != null) {
                      issec = ssec.intValue();
                  }

                  for (java.util.Iterator iterator = SecurityDao.view().iterator(); iterator.hasNext();) {
                      SecurityTo o = (SecurityTo) iterator.next();

                      out.print("\t<option value=\"" + o.getId());

                      if (o.getName().compareTo("public") == 0
                              || o.getId() == issec)
                          out.print("\" selected=\"selected\">");
                      else
                          out.print("\">");

                      out.println(o.getName() + "</option>");
                  }
              %>
          </select>
	  </span>
</div>

<div class="row">
    <span class="label"><label for="location">Location</label></span>
	  <span class="formw">
	  	<select id="location" name="location" size="1">
              <%
                  Integer sloc = (Integer) session.getAttribute("spell.location");
                  int isloc = -1;
                  if (sloc != null) {
                      isloc = sloc.intValue();
                  }

                  for (java.util.Iterator iterator = LocationDao.view().iterator(); iterator.hasNext();) {
                      LocationTo o = (LocationTo) iterator.next();
                      out.print("\t<option value=\"" + o.getId());

                      if (isloc > -1) {
                          if (o.getId() == isloc)
                              out.print("\" selected=\"selected\">");
                          else
                              out.print("\">");
                      } else {
                          if (o.getName().compareTo("Not Specified") == 0)
                              out.print("\" selected=\"selected\">");
                          else
                              out.print("\">");
                      }

                      out.println(o.getName() + "</option>");
                  }
              %>
          </select>
	  </span>
</div>

<div class="row">
    <span class="label"><label for="mood"><a href="moodlist.jsp" title="List of Moods">Mood</a></label></span>
	  <span class="formw">
	  	<select id="mood" name="mood" size="1">
              <%
                  Integer smood = (Integer) session.getAttribute("spell.mood");
                  int ismood = -1;
                  if (smood != null) {
                      ismood = smood.intValue();
                  }
                  // debug
                  out.println("<!- mood " + ismood + " -->");

                  for (java.util.Iterator iterator = MoodDao.view().iterator(); iterator.hasNext();) {
                      MoodTo o = (MoodTo) iterator.next();
                      out.print("\t<option value=\"" + o.getId());

                      if (ismood > -1) {
                          if (o.getId() == ismood)
                              out.print("\" selected=\"selected\">");
                          else
                              out.print("\">");
                      } else {
                          if (o.getName().compareTo("Not Specified") == 0)
                              out.print("\" selected=\"selected\">");
                          else
                              out.print("\">");
                      }

                      out.println(o.getName() + "</option>");
                  }
              %>
          </select>
	  </span>
</div>

<div class="row">
    <span class="label"><label for="music">Music</label></span>
    <span class="formw"><input type="text" name="music" id="music" size="30" value="<%=smusic%>"/></span>
</div>

<div class="row">
			<span class="formw"><input type="checkbox" name="allow_comment" id="allow_comment"
                                       value="checked" checked="checked"/>
			<label for="allow_comment">Allow comments on this entry</label>
			</span>
</div>

<div class="row">
			<span class="formw"><input type="checkbox" name="email_comment" id="email_comment"
                                       value="checked" checked="checked"/>
			<label for="email_comment">Email comments to me</label>
			</span>
</div>

<div class="row">
			<span class="formw"><input type="checkbox" name="aformat" id="aformat"
                                       value="checked" checked="checked"/>
			<label for="aformat">auto-formatting</label>
			</span>
</div>

<!-- Hack to fix spacing problem.. especially with text boxes -->
<div class="spacer">&nbsp;</div>
</fieldset>

<div class="row"><input type="submit" name="submit" value="submit"/></div>

</form>

</div>

</div>

<jsp:include page="footer.inc" flush="false"/>

</body>
</html>