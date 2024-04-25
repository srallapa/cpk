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

import org.junit.*;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.mockito.junit.MockitoJUnitRunner;
import pt.webdetails.cpf.PluginEnvironment;
import pt.webdetails.cpf.PluginSettings;
import pt.webdetails.cpf.plugincall.api.IPluginCall;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mockStatic;
import org.mockito.MockedStatic;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static pt.webdetails.cpk.InterPluginBroker.CDE_RENDER_API_BEAN_ID;
import static pt.webdetails.cpk.InterPluginBroker.CDE_RENDER_API_LEGACY_BEAN_ID;
import static pt.webdetails.cpk.InterPluginBroker.CDE_RENDER_API_RENDER_METHOD;
import static pt.webdetails.cpk.InterPluginBroker.CDE_RENDER_API_BEAN_ID_TAG;
import static pt.webdetails.cpk.InterPluginBroker.CDE_RENDER_API_RENDER_METHOD_TAG;

@RunWith( MockitoJUnitRunner.class )
//@FixMethodOrder(MethodSorters.NAME_ASCENDING
public class InterPluginBrokerTest {
  MockedStatic<PluginEnvironment> mockedStatic;
  //PluginEnvironment environmentMock;
  @Before
  public void setUp() {
    //environmentMock = mock( PluginEnvironment.class );
    mockedStatic = mockStatic(PluginEnvironment.class);
  }

  @After
  public void clear() {
    mockedStatic.close();
  }

  @Test
  public void testRunWithConfiguredValues() throws Exception {
    //try (MockedStatic<PluginEnvironment> mockedStatic = mockStatic(PluginEnvironment.class) ) {
      String beanID = "configured-bean-id";
      String beanMethod = "configured-bean-method";
      String pluginCallResult = "Test with configured values.";

      // mock PluginEnvironment
      PluginEnvironment environmentMock = mock(PluginEnvironment.class);

      doReturn(mockPluginSettings(beanID, beanMethod)).when(environmentMock)
              .getPluginSettings();

      doReturn(mockPluginCall(true, pluginCallResult)).when(environmentMock)
              .getPluginCall(anyString(), eq(beanID), eq(beanMethod));

      //mockedStatic.when(() -> PluginEnvironment.env()).thenReturn(environmentMock);
      when( PluginEnvironment.env()).thenReturn(environmentMock);

      // Test output
      ByteArrayOutputStream out = new ByteArrayOutputStream();
      InterPluginBroker.run(new HashMap<>(), out);

      assertEquals(out.toString(), pluginCallResult);
    //}
  }

  @Test
  public void testRunWithLatestBeanId() throws Exception {
    //try (MockedStatic<PluginEnvironment> mockedStatic = mockStatic(PluginEnvironment.class) ) {
      String pluginCallResult = "Test with the latest bean id.";

      // mock PluginEnvironment
      PluginEnvironment environmentMock = mock(PluginEnvironment.class);
      doReturn(mockPluginSettings(null, null)).when(environmentMock).getPluginSettings();

      doReturn(mockPluginCall(false, null)).when(environmentMock)
              .getPluginCall(anyString(), eq(""), eq(""));

      doReturn(mockPluginCall(true, pluginCallResult)).when(environmentMock)
              .getPluginCall(anyString(), eq(CDE_RENDER_API_BEAN_ID), eq(CDE_RENDER_API_RENDER_METHOD));

      //mockedStatic.when(() -> PluginEnvironment.env()).thenReturn(environmentMock);
      when( PluginEnvironment.env()).thenReturn(environmentMock);

      // Test output
      ByteArrayOutputStream out = new ByteArrayOutputStream();
      InterPluginBroker.run(new HashMap<>(), out);

      assertEquals(out.toString(), pluginCallResult);
    //}
  }

  @Test
  public void testRunWithLegacyBeanId() throws Exception {
    //try (MockedStatic<PluginEnvironment> mockedStatic = mockStatic(PluginEnvironment.class) ) {
      String pluginCallResult = "Test with legacy bean id.";

      // mock PluginEnvironment
      PluginEnvironment environmentMock = mock(PluginEnvironment.class);
      doReturn(mockPluginSettings(null, null)).when(environmentMock).getPluginSettings();

      doReturn(mockPluginCall(false, null)).when(environmentMock)
              .getPluginCall(anyString(), eq(""), eq(""));

      doReturn(mockPluginCall(false, null)).when(environmentMock)
              .getPluginCall(anyString(), eq(CDE_RENDER_API_BEAN_ID), eq(CDE_RENDER_API_RENDER_METHOD));

      doReturn(mockPluginCall(true, pluginCallResult)).when(environmentMock)
              .getPluginCall(anyString(), eq(CDE_RENDER_API_LEGACY_BEAN_ID), eq(CDE_RENDER_API_RENDER_METHOD));

      //mockedStatic.when(() -> PluginEnvironment.env()).thenReturn(environmentMock);
      when( PluginEnvironment.env()).thenReturn(environmentMock);

      // Test output
      ByteArrayOutputStream out = new ByteArrayOutputStream();
      InterPluginBroker.run(new HashMap<>(), out);

      assertEquals(out.toString(), pluginCallResult);
    //}
  }

  private IPluginCall mockPluginCall( boolean exists, String data ) throws Exception {
    IPluginCall pluginCall = mock( IPluginCall.class );

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
