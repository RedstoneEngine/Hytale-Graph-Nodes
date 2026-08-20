(Requires Hyinit to run https://www.curseforge.com/hytale/bootstrap/hyinit)

Meant to be a feature demo of

https://docs.google.com/document/d/1vgdqgi7zafV2PliGISACmBtSrVlJZJH5G-Cj_HL5BWk/edit?tab=t.0#heading=h.1yocw5kjd2ek

where if you assign onto a node prefab-content with graphnode blocks contained within said prefab, it'll spawn new nodes and edges into the graph to work off of based off of the position and rotation of the placed graphnode blocks (with later assigned prefabs on those nodes using those relative rotations.) This incredibly reduces graph size for native implementation which I was originally planning to try out once there was more organizational nodes but Im glad I wont be as it wouldve been a pretty large graph regardless

This is also similar to how Minecraft Jigsaw blocks or Hytale's prefab spawner blocks work(ed) except now with full graph control and hopefully more performant for larger procedural structures

Some pics of it in action

https://discord.com/channels/1440173445039132724/1463689410754379797/1539692945609859112


FYI, there are rough edges everywhere as this took a lot of work, debugging, and setup. Progress may be slow


Side Hytale Fixes:

-Prefabs don't load on graphs correctly due to race conditions

Big Issues:

-I think the read/write bounds of rotated props is what's currently cutting off props

  (Could increase space in base prefabprop but performance would go up for all props...)

Issues:

-PrefabNode doesnt work in graph editor

  (May have to make custom prefab node for connection type or try and parse to first prop reference)

-Too many prefab reads on build()

  (Was stored at json/variable load but the graphnode block ID wasn't present yet occasionally. Marking dirty also didn't work)

-All prefabs need to face 1 direction

  (Current fix is manual rotation, future would be setting the rotation on the content)

-General code cleanup everywhere

ToDo:

-GraphNode blocks will need to disappear on placement like editor_empty

-Need to add a way to set/get the graphNode block contentIds

-Actually make a texture for graphNode blocks

-Content Copy will also need to check if the content should spawn nodes


Future issues that aren't solved:

-Doesnt solve solvers where dead ends can occur (Jigsaws have the same issues, however the mc stronghold always generates a beginning and end)

-Prefabs can intersect (Could be solved by storing prefab bounds within the graph and checking when spawning a prefab but whoof... Thatd be a lot of work and patching. Would be even more if it checks a prefab pool of what can fit which I think is what Minecraft does)