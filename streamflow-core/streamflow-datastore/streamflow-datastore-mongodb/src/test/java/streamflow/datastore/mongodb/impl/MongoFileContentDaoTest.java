/**
 * Copyright 2014 Lockheed Martin Corporation
 *
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
package streamflow.datastore.mongodb.impl;

import com.github.fakemongo.junit.FongoRule;
import streamflow.model.test.IntegrationTest;
import org.junit.Rule;
import org.junit.experimental.categories.Category;

@Category(IntegrationTest.class)
public class MongoFileContentDaoTest {

    @Rule
    public FongoRule fongoRule = new FongoRule();
    
    // TODO: FONGO DOES NOT SUPPORT UNIT TESTS OF GRIDFS: RE-EXAMINE THIS IN THE FUTURE
}