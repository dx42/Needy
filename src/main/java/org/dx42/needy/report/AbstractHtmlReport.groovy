
/*
 * Copyright 2017 the original author or authors.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.dx42.needy.report

import static org.dx42.needy.report.ReportUtil.*

import org.dx42.needy.Dependency
import org.dx42.needy.NeedyVersion

import groovy.xml.StreamingMarkupBuilder

/**
 * Abstract superclass for HTML Reports 
 *
 * @author Chris Mair
 */
abstract class AbstractHtmlReport extends AbstractReport {

	protected static final String STANDARD_TITLE = "Needy Dependency Report"
	protected static final String CSS_FILE = 'htmlreport.css'
	
	String title = "Dependency Report"
	
	abstract protected buildBodySection(List<Dependency> dependencies)
	
	@Override
	void writeReport(Writer writer, List<Dependency> dependencies) {
		def builder = new StreamingMarkupBuilder()
		def html = builder.bind {
			mkp.yieldUnescaped('<!DOCTYPE html>')
			html {
				out << buildHeaderSection()
				out << buildBodySection(dependencies)
			}
		}
		writer << html
	}
	
	protected buildHeaderSection() {
		return {
			head {
				title(STANDARD_TITLE + ": " + title)
				mkp.yieldUnescaped('<meta http-equiv="Content-Type" content="text/html">')
				out << buildCSS()
			}
		}
	}

	protected buildCSS() {
		return {
			def cssInputStream = getClasspathFileInputStream(CSS_FILE)
			def css = cssInputStream.text
			style(type: 'text/css') {
				unescaped << css
			}
		}
	}

	protected buildReportMetadata() {
		return {
			div(class: 'metadata') {
				table {
					tr {
						td(class: 'em', "Report Title:")
						td(class: 'reportTitle', title)
					}
					tr {
						td(class: 'em', "Timestamp:")
						td getFormattedTimestamp()
					}
					tr {
						td(class: 'em', "Generated With:")
						td { a("Needy v" + NeedyVersion.version, href:"https://github.com/dx42/Needy") }
					}
				}
			}
		}
	}

	protected Closure buildDependencyRow(k, v, int index) {
		def applicationNames = v.findAll { name -> includeApplication(name) }
		applicationNames = applicationNames.findAll { name -> !excludeApplication(name) }
		if (!applicationNames) {
			return null
		}
		return {
			tr {
				td(index)
				td(k.group)
				td(k.name)
				td(k.version)
				td(applicationNames.join(", "), class:'applicationNames')
			}
		}
	}

}
