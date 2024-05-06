/*!
 * Copyright 2018 - 2024 Webdetails, a Hitachi Vantara company.  All rights reserved.
 *
 * This software was developed by Webdetails and is provided under the terms
 * of the Mozilla Public License, Version 2.0, or any later version. You may not use
 * this file except in compliance with the license. If you need a copy of the license,
 * please go to  http://mozilla.org/MPL/2.0/. The Initial Developer is Webdetails.
 *
 * Software distributed under the Mozilla Public License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or  implied. Please refer to
 * the license for the specific language governing your rights and limitations.
 */
package pt.webdetails.cpk;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockedStatic;
import org.mockito.junit.MockitoJUnitRunner;
import pt.webdetails.cpf.PluginEnvironment;
import pt.webdetails.cpf.PluginSettings;
import pt.webdetails.cpf.plugincall.api.IPluginCall;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;
import static pt.webdetails.cpk.InterPluginBroker.*;
import static pt.webdetails.cpk.InterPluginBroker.CDE_RENDER_API_RENDER_METHOD;

@RunWith( MockitoJUnitRunner.class )
@Ignore
public class InterPluginBrokerLatestBeanTest {
  MockedStatic<PluginEnvironment> mockedStatic;
  IPluginCall pluginCall;
  @Before
  public void setUp() {
    mockedStatic = mockStatic(PluginEnvironment.class);
  }

  @After
  public void clear() {
    mockedStatic.close();
  }

  @Test
  public void testRunWithLatestBeanId() throws Exception {
      String pluginCallResult = "Test with the latest bean id.";

      // mock PluginEnvironment
      PluginEnvironment environmentMock = mock(PluginEnvironment.class);
      doReturn(mockPluginSettings(null, null)).when(environmentMock).getPluginSettings();

      doReturn(mockPluginCall(false, null)).when(environmentMock)
              .getPluginCall(anyString(), eq(""), eq(""));

      doReturn(mockPluginCall(true, pluginCallResult)).when(environmentMock)
              .getPluginCall(anyString(), eq(CDE_RENDER_API_BEAN_ID), eq(CDE_RENDER_API_RENDER_METHOD));

      mockedStatic.when( PluginEnvironment::env).thenReturn(environmentMock);

      // Test output
      ByteArrayOutputStream out = new ByteArrayOutputStream();
      InterPluginBroker.run(new HashMap<>(), out);

      assertEquals(pluginCallResult, out.toString() );
      pluginCall = null;
  }

  private IPluginCall mockPluginCall( boolean exists, String data ) throws Exception {
    pluginCall = mock( IPluginCall.class );

    doReturn( exists ).when( pluginCall ).exists();
    doReturn( data ).when( pluginCall ).call( any() );

    return pluginCall;
  }

  private PluginSettings mockPluginSettings( String beanId, String beanMethod ) {
    List<String> beanIdList = new ArrayList<>();
    if ( beanId != null ) {
      beanIdList.add( beanId );
    }

    List<String> beanMethodList = new ArrayList<>();
    if ( beanMethod != null ) {
      beanMethodList.add( beanMethod );
    }

    PluginSettings settings = mock( PluginSettings.class );
    doReturn( beanIdList ).when( settings ).getTagValue( eq( CDE_RENDER_API_BEAN_ID_TAG ) );
    doReturn( beanMethodList ).when( settings ).getTagValue( eq( CDE_RENDER_API_RENDER_METHOD_TAG ) );

    return settings;
  }
}
