package com.learning.study;

import com.alibaba.fastjson.JSONObject;
import com.learning.study.model.ListNode;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.util.HashMap;

//@SpringBootTest
@Slf4j
class StudyApplicationTests {

    @Test
    public void test() {
        ListNode l1 = new ListNode(2, new ListNode(4, new ListNode(3, null)));
        ListNode l2 = new ListNode(5, new ListNode(6, new ListNode(4, null)));
        ListNode l3 = addTwoNumbers(l1, l2);
        log.info("l3={}", JSONObject.toJSONString(l3));
    }

    public int[] twoSum(int[] nums, int target) {
        HashMap<Integer, Integer> map = new HashMap<>();
        for (int i = 0; i < nums.length; i++) {
            if (map.containsKey(target - nums[i])) {
                return new int[]{i, map.get(target - nums[i])};
            }
            map.put(nums[i], i);
        }
        return null;
    }

    public ListNode addTwoNumbers(ListNode l1, ListNode l2) {
        ListNode l3 = new ListNode();
        ListNode temp = null;
        int carry = 0;
        do {
            int val1 = l1 == null ? 0 : l1.val;
            int val2 = l2 == null ? 0: l2.val;
            int sum = val1 + val2 + carry;
            if (temp != null) {
                ListNode next3 = new ListNode();
                next3.val = sum % 10;
                temp.next = next3;
                temp = next3;
            } else {
                temp = l3;
                temp.val = sum % 10;
            }
            carry = sum / 10;
            if (l1 != null) {
                l1 = l1.next;
            }
            if (l2 != null) {
                l2 = l2.next;
            }
        } while (l1 != null || l2 != null);
        if (carry > 0) {
            ListNode last = new ListNode();
            last.val = carry;
            temp.next = last;
        }
        return l3;
    }
}
