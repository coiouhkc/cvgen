# cvgen
Scala script to generate a résumé (CV) based on XML input.

**Note**: work-in-progress, use at your own risk!

## usage
### prerequisites
* ```jdk```
* ```scala```
* ```pdflatex```

### input
```
<?xml version="1.0"?>
<cv>
	<section name="General" type="general">
		<entry key="Name">Max Mustermann</entry>
		<entry key="Date of birth">01.01.1991</entry>
	</section>
	<section name="Study">
		<entry key="10/2013 - present">M. Sc. Computer Science
		Somewhere, Far-far-away</entry>
	</section>
	<section name="Professional Activities">
		<entry key="01/2015 - present">CTO @MyCompany</entry>
	</section>
	<projects name="Projects (recent first)">
		<project name="Some project">
			<entry key="description">Your doings during the project</entry>
			<entry key="Customer">A famous and known name</entry>
			<entry key="Technology stack">Angular 2.0 and all that modern stuff</entry>
			<entry key="Tasks">Task1 and cooking coffee</entry>
			<entry key="Duration">10.2016 - present</entry>
		</project>
	</projects>
</cv>
```
### guide
* general section and an entry with the key ```Name``` are mandatory
* general section and further sections till ```projects``` contribute ot the first page of your cv
* project element must have an attribute ```name```
* if not specified above, all further entries are dynamically configurable, just make sure it all fits on the first page of your cv

### build
* Generate ```.tex```
  * ```scala -Dfile.encoding=UTF-8 -Xno-uescape cvgen.scala <input.xml>```
  * produces ```<input.tex>```
* Put your profile image under ```resources/portrait.jpg```
* Generate ```.pdf```
  * ```pdflatex -interaction=batchmode <input.tex>```
