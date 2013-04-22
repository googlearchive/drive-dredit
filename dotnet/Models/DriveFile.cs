/*
 * Copyright (c) 2012 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using Google.Apis.Drive.v2.Data;

namespace DrEdit.Models
{
    public class DriveFile
    {
        public string title { get; set; }
        public string description { get; set; }
        public string mimeType { get; set; }
        public string content { get; set; }

        public DriveFile(File file, string content)
        {
            this.title = file.Title;
            this.description = file.Description;
            this.mimeType = file.MimeType;
            this.content = content;
        }
    }
}
