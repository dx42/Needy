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

import org.dx42.needy.Dependency
import org.dx42.needy.Artifact

class ReportUtil {

	public static final Comparator ARTIFACT_COMPARATOR = { Artifact a1, Artifact a2 ->
		def c1 =  a1.toString().replace(":", " ")
		def c2 =  a2.toString().replace(":", " ")
		return c1.compareToIgnoreCase(c2)
	} as Comparator

	
	static SortedMap<Artifact, Set<String>> buildMapOfArtifactToApplicationNames(List<Dependency> dependencies) {
		SortedMap<Artifact, Set<String>> map = new TreeMap<>(ARTIFACT_COMPARATOR)
		
		dependencies.each { dependency ->
			Artifact key = dependency.artifact
			if (!map.containsKey(key)) {
				map[key] = new TreeSet<String>()
			}
			map[key] << dependency.applicationName
		}
		return map
	}

    static SortedSet<String> buildSetOfApplicationNames(List<Dependency> dependencies) {
        SortedSet<String> names = [] as SortedSet
        dependencies.each { dependency ->
            names << dependency.applicationName
        }
        return names
    }
    
	static InputStream getClasspathFileInputStream(String path) throws IOException {
		def inputStream = ReportUtil.classLoader.getResourceAsStream(path)
		if (!inputStream) {
			throw new FileNotFoundException("File [$path] does not exist or is not accessible")
		}
		inputStream
	}

}
