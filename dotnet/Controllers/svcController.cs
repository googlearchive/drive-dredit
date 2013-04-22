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
using System.Web.Mvc;
using Google.Apis.Authentication;
using Google.Apis.Drive.v2;
using DrEdit.Models;

namespace DrEdit.Controllers
{
    public class svcController : Controller
    {
        //
        // GET: /svc

        public JsonResult svc(string file_id)
        {
            if (string.IsNullOrWhiteSpace(file_id))
            {
                return Json(null, JsonRequestBehavior.AllowGet);
            }

            IAuthenticator authenticator = Session["authenticator"] as IAuthenticator;
            DriveService service = Session["service"] as DriveService;

            if (authenticator == null || service == null)
            {
                // redirect user to authentication
            }

            Google.Apis.Drive.v2.Data.File file = service.Files.Get(file_id).Fetch();
            string data = Utils.DownloadFile(authenticator, file.DownloadUrl);
            DriveFile df = new DriveFile(file, data);
            return Json(df, JsonRequestBehavior.AllowGet);
        }

        //
        // POST: /svc

        [HttpPost, ActionName("svc")]
        public JsonResult svcPost(string title, string description, string mimeType, string content)
        {
            IAuthenticator authenticator = Session["authenticator"] as IAuthenticator;
            DriveService service = Session["service"] as DriveService;

            if (authenticator == null || service == null)
            {
                // redirect user to authentication
            }

            Google.Apis.Drive.v2.Data.File file = Utils.InsertResource(service, authenticator, title, description, mimeType, content);
            return Json(file.Id);
        }

        //
        // PUT: /svc

        [HttpPut, ActionName("svc")]
        public JsonResult svcPut(string title, string description, string mimeType, string content, string resource_id, bool newRevision)
        {
            IAuthenticator authenticator = Session["authenticator"] as IAuthenticator;
            DriveService service = Session["service"] as DriveService;

            if (authenticator == null || service == null)
            {
                // redirect user to authentication
            }

            Google.Apis.Drive.v2.Data.File file;
            if (string.IsNullOrWhiteSpace(resource_id))
            {
                file = Utils.InsertResource(service, authenticator, title, description, mimeType, content);
            }
            else
            {
                file = Utils.UpdateResource(service, authenticator, resource_id, title, description, mimeType, content, newRevision);
            }
            return Json(file.Id);
        }
    }
}
