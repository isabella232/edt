To create an Eclipse product for the running platform:

1. Make sure your target platform has the appropriate Eclipse base and EDT build. For
   example, you could be using the Eclipse IDE for JEE Developers 3.7.1 build, with EDT 0.7.0
   installed into it. This is the basis for the plug-ins that get packaged.

2. Right-click the epp.product file > Export > Plug-in Development > Eclipse Product.

3. Make sure 'Synchr onize before exporting' and 'Generate metadata directory' are both checked,
   specify the destination Directory, then click Finish.
   
   Note: delete any 'juno' update sites in your eclipse workspace before exporting, otherwise for
          some reason they get included. Verify the update sites in the exported package.

4. It will take a few minutes and the result will be an eclipse folder and a repository folder.

5. Zip up the eclipse folder, and then add the repository folder to the 'release' update site.


Notes:

The wizard has a bug (at least on Linux) where a JRE is included regardless of your settings. It should manually be removed
before zipping up the eclipse folder. To remove the JRE from the eclipse folder and drastically reduce file size:
	- delete the 'jre' folder
	- delete 'jre' contents from 'p2/org.eclipse.equinox.p2.core/cache/binary/epp.package.edt_root*'
	  (it's a jar without an extension)
	- update the file size in p2/org.eclipse.equinox.p2.core/cache/artifacts.xml for the modified file


Contents of this package are the JEE package plus EDT, minus:
      <import feature="org.eclipse.jsf.feature"/>
      <import feature="org.eclipse.jst.jsf.apache.trinidad.tagsupport.feature"/>
      <import feature="org.eclipse.jst.jsf.apache.trinidad.tagsupport.feature"/>
      <import feature="org.eclipse.mylyn.bugzilla_feature"/>
      <import feature="org.eclipse.mylyn.context_feature"/>
      <import feature="org.eclipse.mylyn.ide_feature"/>
      <import feature="org.eclipse.mylyn.java_feature"/>
      <import feature="org.eclipse.mylyn.wikitext_feature"/>
      <import feature="org.eclipse.mylyn_feature"/>
      <import feature="org.eclipse.rse"/>
      <import feature="org.eclipse.rse.useractions"/>
      <import feature="org.eclipse.tm.terminal"/>
      <import feature="org.eclipse.tm.terminal.ssh"/>
      <import feature="org.eclipse.tm.terminal.telnet"/>
      <import feature="org.eclipse.tm.terminal.view"/>
