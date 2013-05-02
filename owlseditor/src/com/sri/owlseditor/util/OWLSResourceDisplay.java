/*****************************************************************************************
"The contents of this file are subject to the Mozilla Public License  Version 1.1 
(the "License"); you may not use this file except in compliance with the License.  
You may obtain a copy of the License at http://www.mozilla.org/MPL/

Software distributed under the License is distributed on an "AS IS" basis, 
WITHOUT WARRANTY OF ANY KIND, either express or implied.  See the License for the specific 
language governing rights and limitations under the License.

The Original Code is OWL-S Editor for Protege.

The Initial Developer of the Original Code is SRI International. 
Portions created by the Initial Developer are Copyright (C) 2004 the Initial Developer.  
All Rights Reserved.
 ******************************************************************************************/
package com.sri.owlseditor.util;

import edu.stanford.smi.protegex.owl.model.RDFIndividual;

/**
 * A one-method interface for the instance editor of the main tab and the IOPR
 * manager.
 * 
 * @author Daniel Elenius
 */
public interface OWLSResourceDisplay {

	public void setInstance(RDFIndividual inst);

	public void clearSelection();

}
