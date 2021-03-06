/*
 * Copyright 2011 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.drools.guvnor.client.widgets.wizards;

import org.drools.guvnor.client.explorer.ClientFactory;

import com.google.gwt.event.shared.EventBus;

/**
 * A generic Wizard
 */
public abstract class AbstractWizard<T extends WizardContext>
    implements
    Wizard {

    protected final T                            context;
    protected final ClientFactory                clientFactory;
    protected final EventBus                     eventBus;
    protected final WizardActivityView.Presenter presenter;

    public AbstractWizard(ClientFactory clientFactory,
                          EventBus eventBus,
                          T context,
                          WizardActivityView.Presenter presenter) {
        this.clientFactory = clientFactory;
        this.eventBus = eventBus;
        this.context = context;
        this.presenter = presenter;
    }

}
