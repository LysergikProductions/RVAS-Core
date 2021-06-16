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
import org.jetbrains.annotations.NotNull;

public class CoreException extends Exception {

    private final Class<?> sourceClass;
    private final Method sourceMethod;

    public CoreException(@NotNull Class<?> clazz, @NotNull Throwable thr) {
        super("Fatal exception in " + clazz.getName(), thr);

        this.setStackTrace(thr.getStackTrace());
        this.sourceMethod = null;
        this.sourceClass = clazz;
    }

    public CoreException(@NotNull Method method, @NotNull Throwable thr) {
        super("Fatal exception in " + method.getDeclaringClass().getName() + "." + method.getName(), thr);

        this.setStackTrace(thr.getStackTrace());
        this.sourceMethod = method;
        this.sourceClass = method.getDeclaringClass(); // <- https://stackoverflow.com/a/68006233/12916761
    }

    public Class<?> getSourceClass() { return sourceClass; }
    public Method getSourceMethod() { return sourceMethod; }

    public static boolean isFatalCoreException(Exception exception) {
        if (exception.getClass() != CoreException.class) return false;

        CoreException ce = (CoreException) exception;
        Class<?> source = ce.getSourceClass();

        if (source.isAnnotationPresent(Critical.class))
            return source.getAnnotation(Critical.class).isFatal();

        else return false;
    }

    public static boolean isPhoenixException(Exception exception) {
        if (exception.getClass() != CoreException.class) return false;

        CoreException ce = (CoreException) exception;
        Method method = ce.getSourceMethod();

        if (method.isAnnotationPresent(Phoenix.class))
            return method.getAnnotation(Phoenix.class).doRestart();

        else return false;
    }
}
