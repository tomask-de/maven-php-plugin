/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.phpmaven.plugin.pear;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.StringTokenizer;

/**
 * TODO documentation
 * 
 * 
 * @author mepeisen
 */
public class PearPackageInfo
{
	
	private String pkgName;
	
	private String version;
	
	private List<PearDependency> pkgDependenciesRequired = new ArrayList<PearDependency>();
	
	private List<PearDependency> pkgDependenciesOptional = new ArrayList<PearDependency>();
	
	private String releaseDate;
	
	private List<PearMaintainer> maintainers = new ArrayList<PearMaintainer>();
	
	private List<PearLicense> licenses = new ArrayList<PearLicense>();
	
	private String description;
	
	private String summary;
	
	private String releaseNotes;
	
	private static final String PROLOG_VERSION = "Release Version "; //$NON-NLS-1$
    
    private static final String PROLOG_SUMMARY = "Summary "; //$NON-NLS-1$
    
    private static final String PROLOG_DESCRIPTION = "Description "; //$NON-NLS-1$
    
    private static final String PROLOG_MAINTAINERS = "Maintainers "; //$NON-NLS-1$
    
    private static final String PROLOG_RELEASE_DATE = "Release Date"; //$NON-NLS-1$
    
    private static final String PROLOG_LICENSE = "License "; //$NON-NLS-1$
    
    private static final String PROLOG_RELEASE_NOTES = "Release Notes "; //$NON-NLS-1$
	
	private static final String PROLOG_NAME = "Name "; //$NON-NLS-1$
	
	private static final String PROLOG_REQUIRED = "Required Dependencies "; //$NON-NLS-1$
	
	private static final String PROLOG_OPTIONAL = "Optional Dependencies "; //$NON-NLS-1$
    
    private static final String PROLOG_PACKAGE = "Package "; //$NON-NLS-1$
    
    private static final String PROLOG_PACKAGE_VERSION = "Version "; //$NON-NLS-1$
    
    private static final String PROLOG_PACKAGE_VERSIONS = "Versions "; //$NON-NLS-1$
    
    enum Sections
    {
        UNKNOWN,
        SECTION_VERSION,
        SECTION_SUMMARY,
        SECTION_DESCRIPTION,
        SECTION_MAINTAINERS,
        SECTION_RELEASE_DATE,
        SECTION_LICENSE,
        SECTION_RELEASE_NOTES,
        SECTION_NAME,
        SECTION_REQUIRED,
        SECTION_OPTIONAL
    }
	
	public PearPackageInfo(final String pearOutput, String channel)
	{
		final LineTokenizer tokenizer = new LineTokenizer(pearOutput.trim());
		Sections section = Sections.UNKNOWN;
		StringBuffer description = null;
		StringBuffer summary = null;
		StringBuffer releaseNotes = null;
		String firstLine = null;
		while (tokenizer.hasMoreTokens())
		{
		    String tokenUntrimmed = tokenizer.nextToken();
		    String token = tokenUntrimmed.trim();
		    if (firstLine == null)
		    {
		        firstLine = token;
		    }
	        if (tokenUntrimmed.startsWith(PROLOG_VERSION))
            {
                // we do not expect additional lines in this section
                // section = Sections.SECTION_VERSION;
			    section = Sections.UNKNOWN;
                this.version = new StringTokenizer(token.substring(PROLOG_VERSION.length()).trim(), " ").nextToken(); //$NON-NLS-1$
            }
            else if (tokenUntrimmed.startsWith(PROLOG_NAME))
            {
                // we do not expect additional lines in this section
                // section = Sections.SECTION_NAME;
                section = Sections.UNKNOWN;
                this.pkgName = token.substring(PROLOG_NAME.length()).trim();
            }
            else if (tokenUntrimmed.startsWith(PROLOG_PACKAGE))
            {
                // we do not expect additional lines in this section
                // section = Sections.SECTION_NAME;
                section = Sections.UNKNOWN;
                this.pkgName = token.substring(PROLOG_PACKAGE.length()).trim();
            }
            else if (tokenUntrimmed.startsWith(PROLOG_REQUIRED))
            {
                section = Sections.SECTION_REQUIRED;
                token = token.substring(PROLOG_REQUIRED.length()).trim();
                parseRequiredDep(tokenizer, token);
            }
            else if (tokenUntrimmed.startsWith(PROLOG_OPTIONAL))
            {
                section = Sections.SECTION_OPTIONAL;
                token = token.substring(PROLOG_OPTIONAL.length()).trim();
                parseOptionalDep(tokenizer, token);
            }
            else if (tokenUntrimmed.startsWith(PROLOG_DESCRIPTION))
            {
                section = Sections.SECTION_DESCRIPTION;
                description = new StringBuffer();
                description.append(tokenUntrimmed.substring(PROLOG_DESCRIPTION.length()).trim());
            }
            else if (tokenUntrimmed.startsWith(PROLOG_LICENSE))
            {
                section = Sections.SECTION_LICENSE;
                final PearLicense license = new PearLicense();
                this.licenses.add(license);
                token = tokenUntrimmed.substring(PROLOG_LICENSE.length()).trim();
                if (token.startsWith("(") && token.endsWith(")"))
                {
                    license.setUrl(token.substring(1, token.length() - 1));
                }
                else
                {
                    license.setName(token);
                }
            }
            else if (tokenUntrimmed.startsWith(PROLOG_MAINTAINERS))
            {
                section = Sections.SECTION_MAINTAINERS;
                final PearMaintainer maintainer = new PearMaintainer();
                this.maintainers.add(maintainer);
                token = tokenUntrimmed.substring(PROLOG_MAINTAINERS.length()).trim();
                if (token.startsWith("<") && token.contains(">"))
                {
                    final int indexOf = token.indexOf(">");
                    maintainer.setEmail(token.substring(1, indexOf));
                    if (indexOf < token.length() - 1)
                    {
                        String sub = token.substring(indexOf + 1).trim();
                        if (sub.startsWith("(") && sub.endsWith(")"))
                        {
                            maintainer.setRole(sub.substring(1, sub.length() - 1));
                        }
                        else if (sub.startsWith("("))
                        {
                            String nextToken = null;
                            do
                            {
                                nextToken = tokenizer.nextToken();
                                if (nextToken.startsWith(" " ))
                                {
                                    sub = sub + nextToken.trim();
                                }
                                else
                                {
                                    tokenizer.moveBackward();
                                    break;
                                }
                            }
                            while (!sub.endsWith(")"));
                            maintainer.setRole(sub.substring(1, sub.length() - (sub.endsWith(")") ? 1 : 0)));
                        }
                        else
                        {
                            throw new IllegalStateException("Unable to parse email and role from " + tokenUntrimmed);
                        }
                    }
                }
                else
                {
                    maintainer.setName(token);
                }
            }
            else if (tokenUntrimmed.startsWith(PROLOG_SUMMARY))
            {
                section = Sections.SECTION_SUMMARY;
                summary = new StringBuffer();
                summary.append(tokenUntrimmed.substring(PROLOG_SUMMARY.length()).trim());
            }
            else if (tokenUntrimmed.startsWith(PROLOG_RELEASE_DATE))
            {
                // we do not expect additional lines in this section
                // section = Sections.SECTION_RELEASE_DATE;
                section = Sections.UNKNOWN;
                this.releaseDate = tokenUntrimmed.substring(PROLOG_RELEASE_DATE.length()).trim();
            }
            else if (tokenUntrimmed.startsWith(PROLOG_RELEASE_NOTES))
            {
                section = Sections.SECTION_RELEASE_NOTES;
                releaseNotes = new StringBuffer();
                releaseNotes.append(tokenUntrimmed.substring(PROLOG_RELEASE_NOTES.length()).trim());
            }
            else if (tokenUntrimmed.startsWith(" "))
            {
                if (section == Sections.SECTION_REQUIRED)
    			{
    				this.parseRequiredDep(tokenizer, token);
    			}
    			else if (section == Sections.SECTION_OPTIONAL)
    			{
    				this.parseOptionalDep(tokenizer, token);
    			}
    			else if (section == Sections.SECTION_DESCRIPTION)
                {
                    description.append("\n");
                    description.append(token);
                }
                else if (section == Sections.SECTION_SUMMARY)
                {
                    summary.append("\n");
                    summary.append(token);
                }
                else if (section == Sections.SECTION_RELEASE_NOTES)
                {
                    releaseNotes.append("\n");
                    releaseNotes.append(token);
                }
                else if (section == Sections.SECTION_MAINTAINERS)
                {
                    PearMaintainer maintainer = this.maintainers.get(this.maintainers.size() - 1);
                    token = tokenUntrimmed.substring(PROLOG_MAINTAINERS.length()).trim();
                    if (token.startsWith("<") && token.contains(">"))
                    {
                        if (maintainer.getEmail() != null)
                        {
                            maintainer = new PearMaintainer();
                            this.maintainers.add(maintainer);
                        }
                        final int indexOf = token.indexOf(">");
                        maintainer.setEmail(token.substring(1, indexOf));
                        if (indexOf < token.length() - 1)
                        {
                            String sub = token.substring(indexOf + 1).trim();
                            if (sub.startsWith("(") && sub.endsWith(")"))
                            {
                                maintainer.setRole(sub.substring(1, sub.length() - 1));
                            }
                            else if (sub.startsWith("("))
                            {
                                String nextToken = null;
                                do
                                {
                                    nextToken = tokenizer.nextToken();
                                    if (nextToken.startsWith(" " ))
                                    {
                                        sub = sub + nextToken.trim();
                                    }
                                    else
                                    {
                                        tokenizer.moveBackward();
                                        break;
                                    }
                                }
                                while (!sub.endsWith(")"));
                                maintainer.setRole(sub.substring(1, sub.length() - (sub.endsWith(")") ? 1 : 0)));
                            }
                            else
                            {
                                throw new IllegalStateException("Unable to parse email and role from " + tokenUntrimmed);
                            }
                        }
                    }
                    else
                    {
                        maintainer = new PearMaintainer();
                        this.maintainers.add(maintainer);
                        maintainer.setName(token);
                    }
                }
                else if (section == Sections.SECTION_LICENSE)
                {
                    PearLicense license = this.licenses.get(this.licenses.size() - 1);
                    token = tokenUntrimmed.substring(PROLOG_LICENSE.length()).trim();
                    if (token.startsWith("(") && token.endsWith(")"))
                    {
                        if (license.getUrl() != null)
                        {
                            license = new PearLicense();
                            this.licenses.add(license);
                        }
                        license.setUrl(token.substring(1, token.length() - 1));
                    }
                    else
                    {
                        license = new PearLicense();
                        this.licenses.add(license);
                        license.setName(token);
                    }
                }
            }
            else
            {
                section = Sections.UNKNOWN;
            }
		}
		
		if (description != null)
		{
		    this.description = description.toString();
		}
		if (summary != null)
		{
		    this.summary = summary.toString();
		}
		if (releaseNotes != null)
		{
		    this.releaseNotes = releaseNotes.toString();
		}
		
		if (firstLine != null && this.pkgName != null && (this.version == null || this.version.length() == 0))
		{
		    // no version info; fallback
		    if (firstLine.startsWith("ABOUT " + channel.toUpperCase() + "/" + pkgName.toUpperCase() + "-"))
		    {
		        this.version = firstLine.substring(8 + channel.length() + pkgName.length());
		    }
		    else if (firstLine.startsWith("ABOUT " + pkgName.toUpperCase() + "-"))
            {
                this.version = firstLine.substring(7 + pkgName.length());
            }
		}
	}

    private void parseOptionalDep(final LineTokenizer tokenizer, String token) {
        if (token.startsWith(PROLOG_PACKAGE))
        {
            String packageName = token.substring(PROLOG_PACKAGE.length()).trim(); // maybe a line feed before the package name starts
            if (packageName.length() == 0)
            {
                packageName = tokenizer.nextToken().trim();
            }
            if (tokenizer.hasMoreTokens())
            {
                String token2 = tokenizer.nextToken().trim();
                if (token2.length() == 0) token2 = tokenizer.nextToken().trim(); // maybe an empty line that we fetched first
                if (token2.startsWith(PROLOG_PACKAGE_VERSION))
                {
                    final String version = token2.substring(PROLOG_PACKAGE_VERSION.length(), token2.indexOf(" ", PROLOG_PACKAGE_VERSION.length()));
                    this.pkgDependenciesOptional.add(new PearDependency(packageName, version));
                    return;
                }
                if (token2.startsWith(PROLOG_PACKAGE_VERSIONS))
                {
                    final String version = token2.substring(PROLOG_PACKAGE_VERSIONS.length(), token2.indexOf("-", PROLOG_PACKAGE_VERSIONS.length()));
                    this.pkgDependenciesOptional.add(new PearDependency(packageName, version));
                    return;
                }
                // no package version info
                tokenizer.moveBackward();
            }
            else
            {
                this.pkgDependenciesOptional.add(new PearDependency(packageName, null));
            }
        }
    }

    private void parseRequiredDep(final LineTokenizer tokenizer, String token) {
        if (token.startsWith(PROLOG_PACKAGE))
        {
            String packageName = token.substring(PROLOG_PACKAGE.length()).trim(); // maybe a line feed before the package name starts
            if (packageName.length() == 0)
            {
                packageName = tokenizer.nextToken().trim();
            }
            if (tokenizer.hasMoreTokens())
            {
                String token2 = tokenizer.nextToken().trim();
                if (token2.length() == 0) token2 = tokenizer.nextToken().trim(); // maybe an empty line that we fetched first
                if (token2.startsWith(PROLOG_PACKAGE_VERSION))
                {
                    final String version = token2.substring(PROLOG_PACKAGE_VERSION.length(), token2.indexOf(" ", PROLOG_PACKAGE_VERSION.length()));
                    this.pkgDependenciesRequired.add(new PearDependency(packageName, version));
                    return;
                }
                if (token2.startsWith(PROLOG_PACKAGE_VERSIONS))
                {
                    final String version = token2.substring(PROLOG_PACKAGE_VERSIONS.length(), token2.indexOf("-", PROLOG_PACKAGE_VERSIONS.length()));
                    this.pkgDependenciesRequired.add(new PearDependency(packageName, version));
                    return;
                }
                // no package version info
                tokenizer.moveBackward();
        	}
        	else
        	{
        	    this.pkgDependenciesRequired.add(new PearDependency(packageName, null));
        	}
        }
    }
	
	public String getPkgName() {
		return pkgName;
	}

	public String getVersion() {
		return version;
	}

	public Iterable<PearDependency> getRequiredDependencies()
	{
		return Collections.unmodifiableList(pkgDependenciesRequired);
	}
	
	public Iterable<PearDependency> getOptionalDependencies()
	{
		return Collections.unmodifiableList(pkgDependenciesOptional);
	}
	
	/**
	 * Dependency to other php packages
	 */
	public static final class PearDependency
	{
		
		private String channelName;
		
		private String pkgName;
		
		private String version;
		
		public PearDependency(String name, String version)
		{
			final StringTokenizer tokenizer = new StringTokenizer(name, "/"); //$NON-NLS-1$
			this.channelName = tokenizer.nextToken();
			if (!tokenizer.hasMoreTokens())
			{
			    throw new IllegalStateException("Illegal dependency " + name);
			}
			this.pkgName = tokenizer.nextToken();
			this.version = version;
		}

		public String getChannelName() {
			return channelName;
		}

		public String getPkgName() {
			return pkgName;
		}
		
		public String getVersion() {
		    return this.version;
		}
		
		
	}

    public String getReleaseDate() {
        return releaseDate;
    }

    public Iterable<PearMaintainer> getMaintainers() {
        return Collections.unmodifiableList(maintainers);
    }

    public Iterable<PearLicense> getLicenses() {
        return Collections.unmodifiableList(licenses);
    }

    public String getDescription() {
        return description;
    }

    public String getSummary() {
        return summary;
    }

    public String getReleaseNotes() {
        return releaseNotes;
    }
    
    private static final class LineTokenizer
    {
        /** the position */
        private int position;
        
        /** the lines */
        private final String[] lines;
        
        /**
         * Constructor
         * @param input
         */
        public LineTokenizer(String input)
        {
            this.lines = input.split("\n");
            this.position = 0;
        }
        
        public boolean hasMoreTokens() {
            return this.position < this.lines.length;
        }
        
        public String nextToken() {
            final String result = this.lines[this.position];
            this.position++;
            return result;
        }
        
        public void moveBackward() {
            if (this.position > 0) {
                this.position--;
            }
        }
        
    }

}
