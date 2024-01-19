//package com.hiroki.sheeba.screens.components
//
//import androidx.compose.animation.core.Animatable
//import androidx.compose.animation.core.SpringSpec
//import androidx.compose.animation.core.animateFloatAsState
//import androidx.compose.foundation.background
//import androidx.compose.foundation.layout.Box
//import androidx.compose.foundation.layout.fillMaxSize
//import androidx.compose.foundation.layout.height
//import androidx.compose.foundation.layout.navigationBarsPadding
//import androidx.compose.foundation.layout.padding
//import androidx.compose.foundation.layout.size
//import androidx.compose.foundation.selection.selectable
//import androidx.compose.foundation.shape.RoundedCornerShape
//import androidx.compose.material3.Icon
//import androidx.compose.material3.Text
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.LaunchedEffect
//import androidx.compose.runtime.getValue
//import androidx.compose.runtime.remember
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.draw.clip
//import androidx.compose.ui.draw.shadow
//import androidx.compose.ui.graphics.RectangleShape
//import androidx.compose.ui.layout.LastBaseline
//import androidx.compose.ui.layout.Layout
//import androidx.compose.ui.layout.layoutId
//import androidx.compose.ui.platform.LocalConfiguration
//import androidx.compose.ui.res.painterResource
//import androidx.compose.ui.res.stringResource
//import androidx.compose.ui.text.TextStyle
//import androidx.compose.ui.text.font.FontWeight
//import androidx.compose.ui.text.style.TextAlign
//import androidx.compose.ui.unit.dp
//import androidx.compose.ui.unit.lerp
//import androidx.compose.ui.unit.sp
//import androidx.core.os.ConfigurationCompat
//import androidx.navigation.NavController
//import androidx.navigation.NavDestination
//import androidx.navigation.NavGraph
//import androidx.navigation.compose.currentBackStackEntryAsState
//import com.google.accompanist.insets.navigationBarsPadding
//import com.hiroki.sheeba.util.MenuOptions
//
//private val BottomNavigationElevation = 12.dp
//private val BottomNavigationHeight = 56.dp
//private val BottomNavigationItemHorizontalPadding = 12.dp
//private val CombinedItemTextBaseline = 14.dp
//private val BottomBarTransitionSpec = SpringSpec<Float>(
//    stiffness = 800f,
//    dampingRatio = 0.8f
//)
//
//private val NavGraph.startDestination: NavDestination?
//    get() = findNode(startDestinationId)
//
//private tailrec fun findStartDestination(graph: NavDestination): NavDestination {
//    return if (graph is NavGraph) findStartDestination(graph.startDestination!!) else graph
//}
//
//@Composable
//fun BottomBar(
//    navController: NavController,
//    menus: Array<MenuOptions>,
//) {
//    val navBackStackEntry by navController.currentBackStackEntryAsState()
//    val currentRoute = navBackStackEntry?.destination?.route
//    val sections = remember { MenuOptions.values() }
//    val routes = remember { sections.map { it.route } }
//    if (currentRoute in routes) {
//        val activeColor = MyTheme.colors.active  // Color(0xfff0f9ff)
//        val inactiveColor = MyTheme.colors.inactive  // Color(0xffa1a1aa)
//        val currentSection = sections.first { it.route == currentRoute }
//        Box(
//            modifier = Modifier
//                .shadow(elevation = BottomNavigationElevation, shape = RectangleShape, clip = false)
//                .background(color = MyTheme.colors.uiBackground)  // Color(0xffffffff)
//        ) {
//            BottomBarLayout(
//                selectedIndex = currentSection.ordinal,
//                itemCount = routes.size,
//                modifier = Modifier.navigationBarsPadding(start = false, end = false),
//                indicator = { BottomBarIndicator() }
//            ) {
//                menus.forEach { menu ->
//                    val selected = menu == currentSection
//                    val animationProgress by animateFloatAsState(
//                        targetValue = if (selected) 1f else 0f,
//                        animationSpec = BottomBarTransitionSpec
//                    )
//                    val tintColor = lerp(inactiveColor, activeColor, animationProgress)
//                    BottomBarItem(
//                        icon = {
//                            Icon(
//                                painterResource(id = if (selected) menu.activeIcon else menu.inactiveIcon),
//                                tint = tintColor,
//                                contentDescription = null,
//                                modifier = Modifier.size(20.dp)
//                            )
//                        },
//                        label = {
//                            Text(
//                                text = stringResource(id = menu.title).toUpperCase(
//                                    ConfigurationCompat.getLocales(
//                                        LocalConfiguration.current
//                                    ).get(0)),
//                                color = tintColor,
//                                style = TextStyle(
//                                    fontWeight = FontWeight.Normal,
//                                    fontSize = 11.sp,
//                                    letterSpacing = 0.4.sp
//                                ).copy(textAlign = TextAlign.Center),
//                                maxLines = 1
//                            )
//                        },
//                        selected = selected,
//                        onSelected = {
//                            if (menu.route != currentRoute) {
//                                navController.navigate(menu.route) {
//                                    launchSingleTop = true
//                                    restoreState = true
//                                    popUpTo(findStartDestination(navController.graph).id) {
//                                        saveState = true
//                                    }
//                                }
//                            }
//                        },
//                    )
//                }
//            }
//        }
//    }
//}
//
//@Composable
//fun BottomBarLayout(
//    selectedIndex: Int,
//    itemCount: Int,
//    modifier: Modifier = Modifier,
//    indicator: @Composable () -> Unit,
//    content: @Composable () -> Unit
//) {
//    // Animate the position of the indicator
//    val indicatorIndex = remember { Animatable(0f) }
//    val targetIndicatorIndex = selectedIndex.toFloat()
//    LaunchedEffect(targetIndicatorIndex) {
//        indicatorIndex.animateTo(targetIndicatorIndex, BottomBarTransitionSpec)
//    }
//
//    Layout(
//        modifier = modifier.height(BottomNavigationHeight),
//        content = {
//            Box(Modifier.layoutId("indicator")) {
//                indicator()
//            }
//            content()
//        }
//    ) { measurables, constraints ->
//        val itemWidth = constraints.maxWidth / itemCount
//        val itemPlaceables = measurables
//            .filterNot { it.layoutId == "indicator" }
//            .map { measurable ->
//                measurable.measure(
//                    constraints.copy(
//                        minWidth = itemWidth,
//                        maxWidth = itemWidth
//                    )
//                )
//            }
//        val indicatorPlaceable = measurables
//            .first { it.layoutId == "indicator" }
//            .measure(
//                constraints.copy(minWidth = itemWidth, maxWidth = itemWidth)
//            )
//        layout(
//            width = constraints.maxWidth,
//            height = itemPlaceables.maxByOrNull { it.height }?.height ?: 0
//        ) {
//            // place indicator
//            val indicatorLeft = indicatorIndex.value * itemWidth
//            indicatorPlaceable.placeRelative(x = indicatorLeft.toInt(), y = 0)
//
//            // place tab bar items
//            var x = 0
//            itemPlaceables.forEach { placeable ->
//                placeable.placeRelative(x = x, y = 0)
//                x += placeable.width
//            }
//        }
//    }
//}
//
//@Composable
//fun BottomBarItem(
//    icon: @Composable () -> Unit,
//    label: @Composable () -> Unit,
//    selected: Boolean,
//    onSelected: () -> Unit,
//    modifier: Modifier = Modifier,
//) {
//    Box(
//        modifier = modifier.selectable(selected = selected, onClick = onSelected),
//        contentAlignment = Alignment.Center,
//    ) {
//        BottomBarItemLayout(
//            icon = icon,
//            label = label,
//        )
//    }
//}
//
//@Composable
//fun BottomBarItemLayout(
//    icon: @Composable () -> Unit,
//    label: @Composable (() -> Unit),
//) {
//    Layout({
//        Box(Modifier.layoutId("icon")) { icon() }
//        Box(
//            Modifier
//                .layoutId("label")
//                .padding(horizontal = BottomNavigationItemHorizontalPadding)
//        ) { label() }
//    }) { measurables, constraints ->
//        val iconPlaceable = measurables.first { it.layoutId == "icon" }.measure(constraints)
//        val labelPlaceable = measurables.first { it.layoutId == "label" }.measure(
//            // Measure with loose constraints for height as we don't want the label to take up more
//            // space than it needs
//            constraints.copy(minHeight = 0)
//        )
//
//        val height = constraints.maxHeight
//        val containerWidth = maxOf(labelPlaceable.width, iconPlaceable.width)
//
//        val baseline = labelPlaceable[LastBaseline]
//        val baselineOffset = CombinedItemTextBaseline.roundToPx()
//
//        val labelX = (containerWidth - labelPlaceable.width) / 2
//        val labelY = height - baseline - baselineOffset
//
//        val iconX = (containerWidth - iconPlaceable.width) / 2
//        val iconY = height - (baselineOffset * 2) - iconPlaceable.height
//
//        layout(containerWidth, height) {
//            iconPlaceable.placeRelative(iconX, iconY)
//            labelPlaceable.placeRelative(labelX, labelY)
//        }
//    }
//}
//
//@Composable
//private fun BottomBarIndicator() {
//    Box(
//        modifier = Modifier
//            .fillMaxSize()
//            .height(2.dp)
//            .clip(RoundedCornerShape(10.dp))
//            .background(color = MyTheme.colors.active)  // Color(0xff0ea5e9)
//    )
//}