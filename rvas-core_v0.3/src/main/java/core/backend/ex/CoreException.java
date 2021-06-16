package core.backend.ex;

/* *
 *  About: Custom Exception used in tandem with the 'Critical' and 'Phoenix' annotations
 *
 *  LICENSE: AGPLv3 (https://www.gnu.org/licenses/agpl-3.0.en.html)
 *  Copyright (C) 2021  Lysergik Productions (https://github.com/LysergikProductions)
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *
 * */

import java.lang.reflect.Method;

public class CoreException extends Exception {

    private final Class<?> sourceClass;
    private final Method sourceMethod;

    public CoreException(Class<?> clazz, Throwable thr) {
        super("Fatal exception in " + clazz.getName(), thr);
        this.setStackTrace(thr.getStackTrace());
        this.sourceClass = clazz; this.sourceMethod = null;
    }

    public CoreException(Method sourceMethod, Throwable thr) {
        super("Fatal exception in " + sourceMethod.getDeclaringClass().getName() + sourceMethod.getName(), thr);
        this.setStackTrace(thr.getStackTrace());
        this.sourceMethod = sourceMethod;
        this.sourceClass = this.sourceMethod.getDeclaringClass();
    }

    public Class<?> getSourceClass() { return sourceClass; }
    public Method getSourceMethod() { return sourceMethod; }
}
