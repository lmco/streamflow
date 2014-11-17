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
package streamflow.model;

import java.io.Serializable;

public class PasswordChange implements Serializable {

    String currentPassword;

    String newPassword;

    String confirmPassword;

    
    public PasswordChange() {
    }

    public String getCurrentPassword() {
        return currentPassword;
    }

    public void setCurrentPassword(String currentPassword) {
        this.currentPassword = currentPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 89 * hash + (this.currentPassword != null ? this.currentPassword.hashCode() : 0);
        hash = 89 * hash + (this.newPassword != null ? this.newPassword.hashCode() : 0);
        hash = 89 * hash + (this.confirmPassword != null ? this.confirmPassword.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final PasswordChange other = (PasswordChange) obj;
        if ((this.currentPassword == null) ? (other.currentPassword != null) 
                : !this.currentPassword.equals(other.currentPassword)) {
            return false;
        }
        if ((this.newPassword == null) ? (other.newPassword != null) 
                : !this.newPassword.equals(other.newPassword)) {
            return false;
        }
        if ((this.confirmPassword == null) ? (other.confirmPassword != null) 
                : !this.confirmPassword.equals(other.confirmPassword)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "PasswordChange{" + "currentPassword=" + currentPassword 
                + ", newPassword=" + newPassword + ", confirmPassword=" + confirmPassword + '}';
    }
}
