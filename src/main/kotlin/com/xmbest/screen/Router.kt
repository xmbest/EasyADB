@file:Suppress("DEPRECATION")

package com.xmbest.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.ListItem
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.xmbest.utils.ResourceUtil
import com.xmbest.viewmodel.RouterViewModule

@Composable
fun Router() {
    val viewModel: RouterViewModule = viewModel { RouterViewModule() }
    Row(modifier = Modifier.fillMaxSize()) {
        Left(modifier = Modifier.fillMaxHeight().width(240.dp), viewModel)
        Right(modifier = Modifier.fillMaxHeight().weight(1f), viewModel)
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun Left(modifier: Modifier = Modifier, viewModel: RouterViewModule) {
    val device = viewModel.device.collectAsState().value
    val devices = viewModel.devices.collectAsState().value
    val state = viewModel.index.collectAsState().value
    val devicesListShow = viewModel.deviceListShow.collectAsState().value
    Column(
        modifier.background(MaterialTheme.colors.background).padding(start = 12.dp, end = 12.dp)
    ) {
        viewModel.pageList.forEachIndexed { index, item ->
            Spacer(modifier = Modifier.height(8.dp))
            ListItem(
                modifier = Modifier.height(44.dp).clip(RoundedCornerShape(8.dp)).background(
                    if (index == state) MaterialTheme.colors.primary
                    else MaterialTheme.colors.background
                ).clickable {
                    viewModel.updateIndex(index)
                }, icon = {
                    Icon(
                        painter = painterResource(item.icon),
                        item.icon,
                        tint = optionColor(index == state)
                    )
                }) {
                Text(
                    text = item.name,
                    color = optionColor(index == state)
                )
            }
        }
        Row(
            modifier = Modifier.weight(1f).padding(bottom = 8.dp).fillMaxWidth(),
            verticalAlignment = Alignment.Bottom
        ) {
            ListItem(modifier = Modifier.height(44.dp).clip(RoundedCornerShape(8.dp)).clickable {
                viewModel.updateDeviceListShow(true)
            }, icon = {
                Icon(
                    painter = painterResource(ResourceUtil.iconPath("mobile")),
                    contentDescription = "refresh devices",
                    tint = MaterialTheme.colors.onBackground,
                    modifier = Modifier.clickable {

                    }
                )
            }) {
                Text(
                    device?.serialNumber ?: "请选择设备",
                    maxLines = 2,
                    color = MaterialTheme.colors.onBackground,
                )
            }
        }

        DropdownMenu(
            expanded = devicesListShow, onDismissRequest = {
                viewModel.updateDeviceListShow(!devicesListShow)
            }, modifier = Modifier.width(216.dp)
        ) {
            if (devices.isEmpty()) {
                DropdownMenuItem(onClick = {
                }) {
                    Text(text = "当前设备列表为空")
                }
            } else {
                devices.forEach {
                    DropdownMenuItem(onClick = {
                        viewModel.changeDevice(it)
                        viewModel.updateDeviceListShow(false)
                    }) {
                        Text(text = it.serialNumber)
                    }
                }
            }
        }
    }
}

@Composable
fun Right(modifier: Modifier, viewModel: RouterViewModule) {
    Column(modifier.background(color = MaterialTheme.colors.secondary)) {
        val state = viewModel.index.collectAsState()
        viewModel.pageList[state.value].comp()
    }
}

@Composable
fun optionColor(value: Boolean) =
    if (value) MaterialTheme.colors.onPrimary
    else MaterialTheme.colors.onBackground