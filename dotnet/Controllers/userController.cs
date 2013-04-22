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
using Google.Apis.Oauth2.v2.Data;

namespace DrEdit.Controllers
{
    public class userController : Controller
    {
        //
        // GET: /user

        public JsonResult user()
        {
            IAuthenticator authenticator = Session["authenticator"] as IAuthenticator;

            Userinfo userInfo = Utils.GetUserInfo(authenticator);

            JsonResult result = Json(new { email = userInfo.Email, link = userInfo.Link, picture = userInfo.Picture });
            result.JsonRequestBehavior = JsonRequestBehavior.AllowGet;

            return result;
        }
    }
}
