import java.io._
import scala.xml._

object cvgen {
  def main(args: Array[String]) {

    val cv_xml = args(0)
    val cv_tex = cv_xml.replaceAll("xml", "tex")

    val cv = XML.loadFile(cv_xml)

    def escape(text: String): String =
      text.replace("&", "\\&").replace("#", "\\#").replace("_", "\\_").replace("\n", " \\newline ")

    def entrytext2row(entry: NodeSeq): String = (entry\"@type").text match {
      case "url" => """\""" + """url{""" + entry.text + """}"""
      case "ref" => entry.text.replace("\n", " \\newline")
      case _ => escape(entry.text)
    }

    def entry2row(entry: NodeSeq): String = {
      (entry\"@key").text match {
        case "Name" => """
		\multicolumn{3}{ l } {\LARGE{\textbf{""" + entry.text + """}}} \\
		\multicolumn{3}{ l } {\textbf{Curriculum Vitae}} \\
		\multicolumn{3}{ l } {} \\
"""
        case _ => """
		\nohyphens{\textbf{""" + entry\"@key" + """}} && \nohyphens{""" + entrytext2row(entry) + """} \\
"""
      }
    }

    def p_entry2row(entry: NodeSeq): String = {
      (entry\"@key").text match {
        case "description" => """\multicolumn{3}{ p{0.9\textwidth } } {""" + escape(entry.text) + """} \\
		\multicolumn{3}{ l } {} \\
"""
        case _ => """
		\mbox{\textbf{""" + entry\"@key" + """}} && """ + entrytext2row(entry) + """ \\
"""
      }
    }

    def section2tabular(section: NodeSeq): String = {
      var text = """
	\begin{tabular}{p{0.25\textwidth} p{0.01\textwidth} p{0.7\textwidth}}
"""

      section \ "entry" foreach { entry =>
        text += entry2row(entry)
      }

      text += """
	\end{tabular}
"""

      text
    }

    def project2tabular(section: NodeSeq): String = {
      var text = """
	\begin{tabular}{p{0.25\textwidth} p{0.01\textwidth} p{0.7\textwidth}}
"""

      section \ "entry" foreach { entry =>
        text += p_entry2row(entry)
      }

      text += """
	\end{tabular}
"""

      text
    }



    var text = """
\documentclass[12pt]{article}

\"""+"""usepackage[printwatermark]{xwatermark}
\"""+"""usepackage{xcolor}
\"""+"""usepackage{color}
\"""+"""usepackage{graphicx}
\"""+"""usepackage{wrapfig}
\"""+"""usepackage{hyperref}
\"""+"""usepackage[utf8]{inputenc}
\"""+"""usepackage{hyphenat}
\"""+"""usepackage[margin=2cm]{geometry}

\usepackage{fontspec}

\newwatermark[allpages,color=black!50,angle=90,scale=0.25,xpos=-100,ypos=0]{Generated by \url{https://github.com/coiouhkc/cvgen} }


\begin{document}
\thispagestyle{empty}
\pagestyle{empty}
"""
    // cv

    (cv \\ "section") .filter ( section => (section\"@type").text == "general" ) .foreach { section =>
      val sectionName = section\"@name"

      text += """
\begin{minipage}[t]{\textwidth}""" + section2tabular(section) + """
\end{minipage}

\begin{picture}(0,0)
\put(400,0){\hbox{\includegraphics[height=4cm]{resources/portrait.jpg}}}
\end{picture}
"""
    }

    (cv \\ "section") .filter ( section => (section\"@type").text != "general" ) .foreach { section =>
      val sectionName = section\"@name"
      text += """
\begin{minipage}[t]{\textwidth}
\vskip\medskipamount
{\Large {\textbf{""" + sectionName + """}}}
\vskip\medskipamount
\leaders\vrule width \textwidth\vskip0.4pt
"""

      text += """
""" + section2tabular(section) + """
\end{minipage}
"""

    }

    // new page


    text += """
\newpage"""


    // projects header
    (cv \\ "projects" ) foreach { project =>
      val projectHeader = project\"@name"

      text += """
{\LARGE """ + projectHeader + """}
\vskip\medskipamount
\leaders\vrule width  \textwidth\vskip0.4pt
\vskip\medskipamount
\leaders\vrule width \textwidth\vskip0.4pt
"""
    }


    // project
    (cv \\ "project" ) foreach { project =>
      val projectName = project\"@name"

      text += """
\begin{minipage}[t]{\textwidth}
\vskip\medskipamount
{\Large \textbf{""" + projectName + """}}
\vskip\medskipamount
\leaders\vrule width \textwidth\vskip0.4pt
"""

      text += """
""" + project2tabular(project) + """
\end{minipage}
"""

    }


    text += """
\end{document}
"""

    Some(new PrintWriter(cv_tex)).foreach{p => p.write(text); p.close}

  }
}
