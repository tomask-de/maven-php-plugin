/**
 * Copyright 2010-2012 by PHP-maven.org
 *
 * This file is part of pear-java.
 *
 * pear-java is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * pear-java is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with pear-java.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.phpmaven.pear.library.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.phpmaven.pear.library.ICategory;
import org.phpmaven.pear.library.IPackage;

/**
 * Category implementation
 * 
 * @author Martin Eisengardt <Martin.Eisengardt@googlemail.com>
 * @since 0.1.0
 */
public class Category implements ICategory {
	
	private String href;
	
	private String name;
	
	private String alias;
	
	private String description;
	
	private List<IPackage> packages = new ArrayList<IPackage>();

	@Override
	public String getHRef() {
		return this.href;
	}

	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public void setHRef(String href) {
		this.href = href;
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String getAlias() {
		return this.alias;
	}

	@Override
	public void setAlias(String alias) {
		this.alias = alias;
	}

	@Override
	public String getDescription() {
		return this.description;
	}

	@Override
	public void setDescription(String desc) {
		this.description = desc;
	}

	@Override
	public Iterable<IPackage> getPackages() {
		return Collections.unmodifiableList(this.packages);
	}

	@Override
	public void addPackage(IPackage pkg) {
		this.packages.add(pkg);
	}

}
