/*
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */


package hr.kn.pokemon.api.model;

import POGOProtos.Inventory.InventoryItemDataOuterClass.InventoryItemData;
import POGOProtos.Inventory.InventoryItemOuterClass.InventoryItem;
import POGOProtos.Inventory.Item.ItemDataOuterClass.ItemData;
import POGOProtos.Inventory.Item.ItemIdOuterClass.ItemId;
import POGOProtos.Networking.Requests.RequestTypeOuterClass.RequestType;
import POGOProtos.Networking.Requests.Messages.GetInventoryMessageOuterClass.GetInventoryMessage;
import POGOProtos.Networking.Responses.GetInventoryResponseOuterClass.GetInventoryResponse;
import hr.kn.pokemon.api.client.RequestHandlers;
import hr.kn.pokemon.api.client.ServerRequest;

import java.util.Collection;
import java.util.HashMap;

import com.google.protobuf.InvalidProtocolBufferException;

/**
 * The type Bag.
 */
public class ItemBag {
	private HashMap<ItemId, Item> items;

	/**
	 * Gets item.
	 *
	 * @param type the type
	 * @return the item
	 */
	public Item getItem(ItemId type) {
		if (type == ItemId.UNRECOGNIZED) {
			throw new IllegalArgumentException("You cannot get item for UNRECOGNIZED");
		}

		// prevent returning null
		if (!items.containsKey(type)) {
			return new Item(ItemData.newBuilder().setCount(0).setItemId(type).build());
		}

		return items.get(type);
	}
	
	public ItemBag fetchItemBag(RequestMandatoryParams mandatoryParams) throws InvalidProtocolBufferException {
		items = new HashMap<>();
		GetInventoryMessage invReqMsg = GetInventoryMessage.newBuilder()
				.setLastTimestampMs(0)
				.build();
		ServerRequest inventoryRequest = new ServerRequest(RequestType.GET_INVENTORY, invReqMsg);
		RequestHandlers.myRequestHandler(mandatoryParams).sendServerRequests(inventoryRequest);

		GetInventoryResponse response = null;
		response = GetInventoryResponse.parseFrom(inventoryRequest.getData());

		for (InventoryItem inventoryItem
				: response.getInventoryDelta().getInventoryItemsList()) {
			InventoryItemData itemData = inventoryItem.getInventoryItemData();
			if (itemData.getItem().getItemId() != ItemId.UNRECOGNIZED
					&& itemData.getItem().getItemId() != ItemId.ITEM_UNKNOWN) {
				ItemData item = itemData.getItem();
				addItem(new Item(item));
			}
		}
		
		return this;
	}
	

	public void addItem(Item item) {
		items.put(item.getItemId(), item);
	}


	public Collection<Item> getItems() {
		return items.values();
	}

}
