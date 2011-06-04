/*
Copyright 2008-2011 Gephi
Authors : Luiz Ribeiro <luizribeiro@gmail.com>
Website : http://www.gephi.org

This file is part of Gephi.

Gephi is free software: you can redistribute it and/or modify
it under the terms of the GNU Affero General Public License as
published by the Free Software Foundation, either version 3 of the
License, or (at your option) any later version.

Gephi is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Affero General Public License for more details.

You should have received a copy of the GNU Affero General Public License
along with Gephi.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.gephi.scripting.wrappers;

import org.gephi.graph.api.Edge;
import org.gephi.scripting.util.GyNamespace;
import org.python.core.Py;
import org.python.core.PyFloat;
import org.python.core.PyInteger;
import org.python.core.PyObject;
import org.python.core.PyString;

/**
 *
 * @author Luiz Ribeiro
 */
public class GyEdge extends PyObject {

    private GyNamespace namespace;
    private Edge edge;
    // Hack to get color and size attributes into jythonconsole's auto-completion
    // TODO: get rid of this ugly hack (:
    public float weight;
    public String label;

    public GyEdge(GyNamespace namespace, Edge edge) {
        this.namespace = namespace;
        this.edge = edge;
    }

    @Override
    public String toString() {
        return GyNamespace.EDGE_PREFIX + Integer.toString(edge.getId());
    }

    public Edge getEdge() {
        return edge;
    }

    @Override
    public void __setattr__(String name, PyObject value) {
        if (name.equals("weight")) {
            float size = (Float) value.__tojava__(Float.class);
            edge.getEdgeData().getAttributes().setValue("Weight", size);
        } else if (name.equals("label")) {
            String label = (String) value.__tojava__(String.class);
            edge.getEdgeData().setLabel(label);
        } else if (!name.startsWith("__")) {
            Object obj = null;

            // TODO: support conversions for other object types
            if (value instanceof PyString) {
                obj = (String) value.__tojava__(String.class);
            } else if (value instanceof PyInteger) {
                obj = (Integer) value.__tojava__(Integer.class);
            } else if (value instanceof PyFloat) {
                obj = (Float) value.__tojava__(Float.class);
            }

            if (obj == null) {
                throw Py.AttributeError("Unsupported edge attribute type '" + value.getType().getName() + "'");
            }

            edge.getEdgeData().getAttributes().setValue(name, obj);
        } else {
            super.__setattr__(name, value);
        }
    }

    @Override
    public PyObject __findattr_ex__(String name) {
        if (name.equals("weight")) {
            float weight = (Float) edge.getEdgeData().getAttributes().getValue("Weight");
            return Py.java2py(weight);
        } else if (name.equals("label")) {
            return Py.java2py(edge.getEdgeData().getLabel());
        } else if (!name.startsWith("__")) {
            Object obj = edge.getEdgeData().getAttributes().getValue(name);
            if (obj == null) {
                return null;
            }
            return Py.java2py(obj);
        } else {
            return super.__findattr_ex__(name);
        }
    }
}
